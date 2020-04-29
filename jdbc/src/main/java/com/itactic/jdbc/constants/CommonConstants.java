package com.itactic.jdbc.constants;

/**
 * 
 */

public class CommonConstants {

	public static final int DEF_PAGE_SIZE=10; //默认每页行数
	public static final int DEF_PAGE_INDEX=1; //默认当前页号
	
	/**
	 * bootgrid分页相关默认设置
	 */
	public static final Integer BOOTGRID_CURRENT_DEFAULT = 1;//当前页
	public static final Integer BOOTGRID_ROWCOUNT_DEFAULT = 10;//每页记录数
	
	/**
	 * 数据源标志类型
	 */
	public interface DATASOURCE_TYPE{
		/**
		 * 默认
		 */
		public static final String DEFAULT = "default";
	}

	/**
	 * 动态表明
	 * */
	public static final String DYNAMIC = "${dynamic}";
	
}
