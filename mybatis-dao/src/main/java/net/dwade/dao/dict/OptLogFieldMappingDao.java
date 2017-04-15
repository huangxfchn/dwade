package net.dwade.dao.dict;

import net.dwade.domain.dict.OptLogFieldMapping;

public interface OptLogFieldMappingDao {
    int deleteByPrimaryKey(String id);

    int insert(OptLogFieldMapping record);

    int insertSelective(OptLogFieldMapping record);

    OptLogFieldMapping selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OptLogFieldMapping record);

    int updateByPrimaryKey(OptLogFieldMapping record);
}