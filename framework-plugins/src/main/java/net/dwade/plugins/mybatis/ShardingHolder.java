package net.dwade.plugins.mybatis;

/**
 * 分表条件的ThreadLocal，每次sql操作需要重新调用set方法设置
 * @author huangxf
 * @date 2017年6月30日
 */
public class ShardingHolder {
	
	private static ThreadLocal<String[]> tablesHolder = new ThreadLocal<String[]>();
	
	/**
	 * 设置分表条件，例如ShardingHolder.set( "20170704" )
	 * @param tables
	 */
	public static void set( String... suffix ) {
		tablesHolder.set( suffix );
	}
	
	/**
	 * 获取当前线程设置的分表条件
	 * @return
	 */
	public static String[] get() {
		return tablesHolder.get();
	}
	
	/**
	 * 从ThreadLocal中移除分表条件
	 */
	public static void remove() {
		tablesHolder.remove();
	}

}
