/**
 * 
 */
package net.dwade.plugins.mybatis.parser;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

/**
 * @author fangjialong
 * @date 2015年9月5日 下午3:54:27
 */
public class DeleteSqlParser implements SqlParser {

	private boolean inited = false;

	private Delete statement;

	private List<Table> tables = new ArrayList<Table>();

	public DeleteSqlParser(Delete statement) {
		this.statement = statement;
	}

	@Override
	public List<Table> getTables() {
		return tables;
	}

	public void init() {
		if (inited) {
			return;
		}
		inited = true;
		tables.add(statement.getTable());
	}

	@Override
	public String toSQL() {
		StatementDeParser deParser = new StatementDeParser(new StringBuilder());
		statement.accept(deParser);
		return deParser.getBuffer().toString();
	}

}