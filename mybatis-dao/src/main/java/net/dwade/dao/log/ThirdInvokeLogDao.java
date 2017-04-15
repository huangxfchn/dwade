package net.dwade.dao.log;

import net.dwade.domain.log.ThirdInvokeLog;

public interface ThirdInvokeLogDao {
    int deleteByPrimaryKey(String id);

    int insert(ThirdInvokeLog record);

    int insertSelective(ThirdInvokeLog record);

    ThirdInvokeLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ThirdInvokeLog record);

    int updateByPrimaryKey(ThirdInvokeLog record);
}