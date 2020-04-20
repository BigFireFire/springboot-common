/**
 * 
 */
package com.itactic.jdbc.utils;



import com.itactic.jdbc.jdbc.DbType;

import java.util.HashMap;
import java.util.Map;

public class PageUtils {
	public static final String ORACLE_PAGE_1 = "SELECT * FROM ( SELECT A.*, ROWNUM RN FROM (";
	public static final String ORACLE_PAGE_2 = ") A WHERE ROWNUM <= ";
	public static final String ORACLE_PAGE_3 = ") WHERE RN >= ";
	private static final String DB2_PAGE_1 = "SELECT * FROM (  SELECT B.*, ROWNUMBER() OVER() AS RN FROM (";
	private static final String DB2_PAGE_2 = ") AS B )AS A WHERE A.RN BETWEEN ";
	private static final String DB2_PAGE_3 = " AND ";
	
	/**
	 * 分页查询
	 * @param sql
	 * @param page
	 * @param rows
	 * @param dbType
	 * @return
	 */
	public static String assemblePageSQL(String sql, int page, int rows, DbType dbType) {
		if(dbType == DbType.ORACLE){
			return assmbleOraclePageSQL(sql, page, rows);
		}else if(dbType == DbType.DB2){
			return assmbleDB2PageSQL(sql, page, rows);
		}else if(dbType == DbType.MYSQL){
			return assmbleMySQLPageSQL(sql, page, rows);
		}
		return assmbleOraclePageSQL(sql, page, rows);
	}

	public static String assmbleOracleCount(String sql) {
		return "SELECT COUNT(1) FROM (" + sql + ") A";
	}

	public static String assmbleOraclePageSQL(String sql, int page, int rows) {
		StringBuffer bf = new StringBuffer(ORACLE_PAGE_1).append(sql).append(
				ORACLE_PAGE_2).append(String.valueOf(page * rows)).append(
				ORACLE_PAGE_3).append(String.valueOf((page - 1) * rows + 1));
		return bf.toString();
	}

	/**
	 * 用户NamedParameterJdbcTemplate组装Oracle分页SQL
	 * @param sql  输入sql
	 * @return   返回组装好的sql
	 */
	public static String assembleOraclePageSQLByName(String sql) {
		return  new StringBuffer(ORACLE_PAGE_1).append(sql).append(
				ORACLE_PAGE_2).append(":page*:rows").append(
				ORACLE_PAGE_3).append("((:page - 1) * :rows + 1)").toString();
	}
	
	public static String assmbleMySQLCount(String sql) {
		return "SELECT COUNT(1) FROM (" + sql + ") A";
	}
	
	public static String assmbleDB2Count(String sql) {
		return "SELECT COUNT(*) FROM (" + sql + ") A with ur";
	}
	
	public static String assmbleDB2PageSQL(String sql, int page, int rows) {
		StringBuffer bf = new StringBuffer();
		bf.append(DB2_PAGE_1).append(sql).append(DB2_PAGE_2).append(
				String.valueOf((page - 1) * rows + 1)).append(DB2_PAGE_3)
				.append(String.valueOf(page * rows)).append(" with ur");
		return bf.toString();
	}
	
	public static String assmbleMySQLPageSQL(String sql,int page, int rows){
		page = page <= 1 ? 1 : page;
		return sql + " LIMIT "+(page-1)*rows+", "+rows;
	}
	
	public static Map<String, String> assmbleOrderBy(Map<String, Object> map){
		Map<String, String> m = new HashMap<String,String>();
		for(Map.Entry<String, Object> entry : map.entrySet()){
			if(entry.getKey().startsWith("sort[")){
				 m.put("_order_column", entry.getKey().substring(5, entry.getKey().length()-1));
				 m.put("_order_sort", entry.getValue().toString());
			}
		}
		return m;
	}

}
