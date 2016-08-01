package org.thiki.kanban.teamMembers;

import org.springframework.stereotype.Service;
import org.thiki.kanban.team.Team;
import org.thiki.kanban.team.TeamsService;

import javax.annotation.Resource;
import java.security.InvalidParameterException;
import java.text.MessageFormat;

/**
 * Created by 濤 on 7/26/16.
 */
@Service
public class TeamMembersService {
    @Resource
    private TeamMembersPersistence teamMembersPersistence;
    @Resource
    private TeamsService teamsService;

    public TeamMember joinTeam(String teamId, final TeamMember teamMember, String userName) {
        Team targetTeam = teamsService.findById(teamId);
        if (targetTeam == null) {
            throw new InvalidParameterException(MessageFormat.format("Team {0} is not found.", teamId));
        }

        TeamMember foundMember = teamMembersPersistence.findMemberByTeamId(teamMember.getMember(), teamId);
        if (foundMember != null) {
            throw new InvalidParameterException(MessageFormat.format("Member named {0} is already in the team.", teamMember.getMember()));
        }

        teamMember.setReporter(userName);
        teamMember.setTeamId(teamId);
        teamMembersPersistence.joinTeam(teamMember);
        return teamMembersPersistence.findById(teamMember.getId());
    }
}