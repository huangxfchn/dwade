package net.dwade.plugins.mybatis;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dwade.plugins.mybatis.parser.JSqlParserFactory;
import net.dwade.plugins.mybatis.parser.SqlParser;
import net.dwade.plugins.mybatis.parser.SqlParserFactory;
import net.sf.jsqlparser.schema.Table;

/**
 * mybatis分表拦截器，<em>如果同时和分页插件一起使用，需要配置在分页插件之后</em><br/>
 * <p>mybatis拦截器的执行顺序：Executor-->StatementHandler(ParameterHandler)-->ResultSetHandler</p>
 * <p>
 * 该分表插件拦截了Executor的query和update方法，Executor执行完毕之后将分表条件清除，
 * 否则会把全表操作误认为分表操作，此外，由于分页插件也拦截了Executor的query方法，因此和分页插件同时
 * 使用时需要将分页插件配置在该分表插件前面，因为InterceptorChain.pluginAll(Object target)返回的
 * 是最后一个拦截器的代理，因此会先执行最后一个拦截器的intercept方法
 * </p>
 * <p>为了避免对非日表的操作带来影响，该插件在Executor执行完毕的时候清除ThreadLocal中的分表条件。</p>
 * <strong>为什么不在获取分表条件之后就清理ThreadLocal中的分表条件？</strong>
 * 因为分页插件拦截的是Executor，并且自己创建了BoundSql进行调用，先是count操作，再是查询数据，如果拦截的是获取之后就清除，
 * 那么只会对count操作的分表起作用，对分页插件的数据查询操作是不会起作用的
 * @author huangxf
 * @date 2017年6月29日
 */
@Intercepts({ 
	@Signature(type = StatementHandler.class, method = "prepare", args = { java.sql.Connection.class, Integer.class }),
	@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class ShardingInterceptor implements Interceptor {
	
	private Logger logger = LoggerFactory.getLogger( this.getClass() );
	
	private final SqlParserFactory parserFactory = new JSqlParserFactory();
	
	private final Field boundSqlField;
	
	public final String DEFAULT_SEPARATOR = "_";
	
	/**
	 * 分表的连接符，T_ORDER_20160629，其中T_ORDER为逻辑表名，_代表separator
	 */
	private String separator = DEFAULT_SEPARATOR;

	public ShardingInterceptor() {
		try {
			boundSqlField = BoundSql.class.getDeclaredField("sql");
			boundSqlField.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		
		//---------------------------------------------------------------
		// 对于分页插件而言，它自己调用了count的SQL查询，最后还是会进入intercept方法，只不过
		// invocation的target是StatementHandler了，而不再是Executor
		//---------------------------------------------------------------
		if ( invocation.getTarget() instanceof Executor ) {
			try {
				return invocation.proceed();
			} finally {
				ShardingHolder.remove();
			}
		}
		
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		BoundSql boundSql = statementHandler.getBoundSql();
		
		// 判断是否设置分表条件，ThreadLocal中的变量在commit或者rollback的时候清除
		final String[] actualTables = ShardingHolder.get();
		if ( ArrayUtils.isEmpty( actualTables ) ) {
			return invocation.proceed();
		}
		
		// 进行SQL解析
		SqlParser sqlParser = parserFactory.createParser( boundSql.getSql() );
		List<Table> tables = sqlParser.getTables();
		if ( tables.isEmpty() ) {
			return invocation.proceed();
		}
		
		// 如果设置的表名数量和实际不一致，抛出SQL异常
		if ( tables.size() != actualTables.length ) {
			throw new SQLException( "Table sharding exception, tables in sql not equals to actual settings" );
		}
		
		// 设置实际的表名
		for ( int index = 0; index < tables.size(); index++ ) {
			if ( StringUtils.isEmpty( actualTables[ index ] ) ) {
				continue;
			}
			Table table = tables.get( index );
			String targetName = table.getName() + separator + actualTables[ index ];
			logger.info( "Sharding table, {}-->{}", table, targetName );
			table.setName( targetName );
		}
		
		// 修改实际的SQL
		String targetSQL = sqlParser.toSQL();
		boundSqlField.set( boundSql, targetSQL );
		
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap( target, this );
	}

	@Override
	public void setProperties(Properties properties) {
		this.separator = properties.getProperty( "separator", DEFAULT_SEPARATOR );
	}

}
