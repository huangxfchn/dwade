package net.dwade.plugins.mybatis.parser;

import java.util.List;

import net.sf.jsqlparser.schema.Table;

/**
 * SQL解析器
 * @author huangxf
 * @date 2017年6月30日
 */
public interface SqlParser {

	/**
	 * 如果解析出来的sql中没有表，则返回空List，而不是null
	 * @return
	 */
	public List<Table> getTables();
	
	public String toSQL();

}
