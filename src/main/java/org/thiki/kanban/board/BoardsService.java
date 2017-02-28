package org.thiki.kanban.board;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.thiki.kanban.foundation.exception.BusinessException;
import org.thiki.kanban.foundation.exception.ResourceNotFoundException;
import org.thiki.kanban.projects.project.Project;
import org.thiki.kanban.projects.members.MembersService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xubitao on 05/26/16.
 */
@Service
public class BoardsService {

    @Resource
    private BoardsPersistence boardsPersistence;
    @Resource
    private MembersService membersService;

    @CacheEvict(value = "board", key = "startsWith('#userName + boards')", allEntries = true)
    public Board create(String userName, final Board board) {
        boolean isExists = boardsPersistence.unique(board.getId(), board.getName(), userName);
        if (isExists) {
            throw new BusinessException(BoardCodes.BOARD_IS_ALREADY_EXISTS);
        }
        board.setAuthor(userName);
        boardsPersistence.create(board);
        return boardsPersistence.findById(board.getId());
    }

    public Board findById(String id) {
        return boardsPersistence.findById(id);
    }

    @Cacheable(value = "board", key = "#userName+'boards-personal'")
    public List<Board> loadBoards(String userName) {
        List<Board> personalBoards = boardsPersistence.findPersonalBoards(userName);
        List<Board> projectsBoards = loadProjectBoards(userName);

        List<Board> boards = new ArrayList<>();
        boards.addAll(personalBoards);
        boards.addAll(projectsBoards);
        return boards;
    }

    @Cacheable(value = "board", key = "#userName+'boards-teams'")
    private List<Board> loadProjectBoards(String userName) {
        List<Board> projectsBoards = new ArrayList<>();
        List<Project> projects = membersService.loadProjectsByUserName(userName);
        for (Project project : projects) {
            List<Board> projectBoards = boardsPersistence.findProjectsBoards(project.getId());
            projectsBoards.addAll(projectBoards);
        }
        return projectsBoards;
    }

    @CacheEvict(value = "board", key = "contains(#userName)", allEntries = true)
    public Board update(String userName, Board board) {
        Board boardToDelete = boardsPersistence.findById(board.getId());
        if (boardToDelete == null) {
            throw new ResourceNotFoundException(BoardCodes.BOARD_IS_NOT_EXISTS);
        }
        boolean isExists = boardsPersistence.unique(board.getId(), board.getName(), userName);
        if (isExists) {
            throw new BusinessException(BoardCodes.BOARD_IS_ALREADY_EXISTS);
        }
        boardsPersistence.update(board);
        return boardsPersistence.findById(board.getId());
    }

    @CacheEvict(value = "board", key = "contains('#board.id')", allEntries = true)
    public int deleteById(String boardId, String userName) {
        Board boardToDelete = boardsPersistence.findById(boardId);
        if (boardToDelete == null || !boardToDelete.isOwner(userName)) {
            throw new ResourceNotFoundException(BoardCodes.BOARD_IS_NOT_EXISTS);
        }
        return boardsPersistence.deleteById(boardId, userName);
    }

    public boolean isBoardOwner(String boardId, String userName) {
        Board board = findById(boardId);
        if (board == null) {
            throw new BusinessException(BoardCodes.BOARD_IS_NOT_EXISTS);
        }
        if (board.isOwner(userName)) {
            return true;
        }
        boolean isTeamMember = membersService.isMember(board.getProjectId(), userName);
        return isTeamMember && board.getOwner().equals(userName);
    }
}
