package net.dwade.plugins.mybatis.parser;

import java.sql.SQLException;

/**
 * SQL解析器工厂
 * @author huangxf
 * @date 2017年6月30日
 */
public interface SqlParserFactory {
	
	public SqlParser createParser(String originalSql) throws SQLException;

}
