package net.dwade.plugins.mybatis.parser;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

/**
 * insert 
 * @author huangxf
 * @date 2017年6月30日
 */
public class InsertSqlParser implements SqlParser {

	private boolean inited = false;

	private Insert statement;

	private List<Table> tables = new ArrayList<Table>();

	public InsertSqlParser(Insert statement) {
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
