package org.thiki.kanban.procedure;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by xubitao on 04/26/16.
 */

@Repository
public interface ProceduresPersistence {
    Integer create(Procedure procedure);

    Procedure findById(@Param("id") String id);

    List<Procedure> loadByBoardId(String boardId);

    Integer update(Procedure procedure);

    Integer deleteById(@Param("id") String id);

    Boolean checkRedundancy(Procedure procedure);

    void resort(Map<String, Object> procedure);
}