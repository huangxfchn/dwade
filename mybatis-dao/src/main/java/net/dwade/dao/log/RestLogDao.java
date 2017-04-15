package net.dwade.dao.log;

import net.dwade.domain.log.RestLog;

public interface RestLogDao {
    int deleteByPrimaryKey(String id);

    int insert(RestLog record);

    int insertSelective(RestLog record);

    RestLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(RestLog record);

    int updateByPrimaryKey(RestLog record);
}