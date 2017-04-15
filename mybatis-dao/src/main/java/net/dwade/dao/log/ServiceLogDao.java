package net.dwade.dao.log;

import net.dwade.domain.log.ServiceLog;

public interface ServiceLogDao {
    int deleteByPrimaryKey(String id);

    int insert(ServiceLog record);

    int insertSelective(ServiceLog record);

    ServiceLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ServiceLog record);

    int updateByPrimaryKey(ServiceLog record);
}