package net.dwade.dao.log;

import net.dwade.domain.log.ExceptionLog;

public interface ExceptionLogDao {
    int deleteByPrimaryKey(String id);

    int insert(ExceptionLog record);

    int insertSelective(ExceptionLog record);

    ExceptionLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ExceptionLog record);

    int updateByPrimaryKey(ExceptionLog record);
}