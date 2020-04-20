package com.itactic.jdbc.jdbc;

import com.itactic.jdbc.constants.CommonConstants;
import com.itactic.jdbc.exception.SqlBuilderException;
import com.itactic.jdbc.utils.PageUtils;
import com.itactic.jdbc.jdbc.Id.DBType;
import com.itactic.jdbc.jdbc.Id.GenerationType;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

public class SqlBuilder {

	private Class<?> cls = null;

	private Set<String> criteria = null;

	private Set<String> update_property = null;

	private Map<String, Object> params = null;

	private String order = null;

	private String orderType = null;

	private String orderBy = null;

	private Integer page = null;

	private Integer rows = null;

	private String uuid;

	private String dataSource;

	private DbType dbType;

	protected Map<String, String> columnMapping = new HashMap<String, String>();
	protected Map<String, String> propertyMapping = new HashMap<String, String>();

	private String idName;
	private String[] pks;

	private Boolean batchUpdate = false;

	public Boolean getBatchUpdate() {
		return batchUpdate;
	}

	public void setBatchUpdate(Boolean batchUpdate) {
		this.batchUpdate = batchUpdate;
	}

	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public String[] getPks() {
		return pks;
	}

	public void setPks(String[] pks) {
		this.pks = pks;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	private SqlBuilder(Class<?> cls) {
		this.cls = cls;
		criteria = new HashSet<String>();
		update_property = new HashSet<String>();
		params = new HashMap<String, Object>();
		initDataSource();
		initColumns();
		initPks();
	}

	protected void initPks() {
		Table annotation = this.cls.getAnnotation(Table.class);
		if (StringUtils.isNotBlank(annotation.pks())) {
			setPks(annotation.pks().split(","));
		} else {
			if (StringUtils.isNotBlank(this.idName)) {
				setPks(new String[] { this.idName });
			}
		}
	}

	protected void initDataSource() {
		DataSourceType annotation = this.cls.getAnnotation(DataSourceType.class);
		if (null != annotation) {
			setDataSource(annotation.value());
			setDbType(annotation.type());
		} else {
			setDataSource(CommonConstants.DATASOURCE_TYPE.DEFAULT);
			setDbType(DbType.MYSQL);
		}
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public DbType getDbType() {
		return dbType;
	}

	public void setDbType(DbType dbType) {
		this.dbType = dbType;
	}

	protected void initColumns() {
		Field[] fields = this.cls.getDeclaredFields();
		for (Field field : fields) {
			Column annotation = field.getAnnotation(Column.class);
			if (null != annotation) {
				String columnName = annotation.value();
				String propertyName = field.getName();
				columnMapping.put(columnName, propertyName);
				propertyMapping.put(propertyName, columnName);
			}
			Id idAnnotation = field.getAnnotation(Id.class);
			if (null != idAnnotation) {
				setIdName(field.getName());
			}
		}
	}

	protected String getColumnNameByPropertyName(String propertyName) {
		return null == propertyMapping.get(propertyName) ? underscoreName(propertyName)
				: propertyMapping.get(propertyName);
	}

	public static SqlBuilder build(Class<?> cls) {
		return new SqlBuilder(cls);
	}

	public SqlBuilder update(String property, Object value) {
		if (value != null) {
			update_property.add(getColumnNameByPropertyName(property) + "=:" + property);
			params.put(property, value);
		}
		return this;
	}

	public SqlBuilder addParams(Object obj) {
		Class<?> cls = obj.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			if ("serialVersionUID".equals(field.getName())) {
				continue;
			}
			String name = field.getName();
			String strGet = "get" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
			Method methodGet;
			try {
				methodGet = cls.getDeclaredMethod(strGet);
				Object object = methodGet.invoke(obj);
				params.put(getColumnNameByPropertyName(name), object);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return this;
	}

	public SqlBuilder eq(String property, Object value) {
		if (value != null) {
			if (value instanceof String && value == "") {
				return this;
			}
			criteria.add(getColumnNameByPropertyName(property) + "=:" + property);
			params.put(property, value);
		}
		return this;
	}

	public SqlBuilder neq(String property, Object value) {
		if (value != null) {
			if (value instanceof String && value == "") {
				return this;
			}
			criteria.add(getColumnNameByPropertyName(property) + "!=:" + property);
			params.put(property, value);
		}
		return this;
	}

	public SqlBuilder like(String property, Object value) {
		if (value != null) {
			if (value instanceof String && value == "") {
				return this;
			}
			criteria.add(getColumnNameByPropertyName(property) + " LIKE '%" + value + "%'");
		}
		return this;
	}

	public SqlBuilder lLike(String property, Object value) {
		if (value != null) {
			if (value instanceof String && value == "") {
				return this;
			}
			criteria.add(getColumnNameByPropertyName(property) + " LIKE '" + value + "%'");
		}
		return this;
	}

	public SqlBuilder rLike(String property, Object value) {
		if (value != null) {
			if (value instanceof String && value == "") {
				return this;
			}
			criteria.add(getColumnNameByPropertyName(property) + " LIKE '%" + value + "'");
		}
		return this;
	}

	public SqlBuilder lt(String property, Object value) {
		if (value != null) {
			if (value instanceof String && value == "") {
				return this;
			}
			String _property = property + "_lt";
			criteria.add(getColumnNameByPropertyName(property) + "<:" + _property);
			params.put(_property, value);
		}
		return this;
	}

	public SqlBuilder gt(String property, Object value) {
		if (value != null) {
			if (value instanceof String && value == "") {
				return this;
			}
			String _property = property + "_gt";
			criteria.add(getColumnNameByPropertyName(property) + ">:" + _property);
			params.put(_property, value);
		}
		return this;
	}

	public SqlBuilder lte(String property, Object value) {
		if (value != null) {
			if (value instanceof String && value == "") {
				return this;
			}
			String _property = property + "_lte";
			criteria.add(getColumnNameByPropertyName(property) + "<=:" + _property);
			params.put(_property, value);
		}
		return this;
	}

	public SqlBuilder gte(String property, Object value) {
		if (value != null) {
			if (value instanceof String && value == "") {
				return this;
			}
			String _property = property + "_gte";
			criteria.add(getColumnNameByPropertyName(property) + ">=:" + _property);
			params.put(_property, value);
		}
		return this;
	}

	public SqlBuilder in(String property, Object value) {
		if (value != null) {
			String _property = property + "_in";
			criteria.add(getColumnNameByPropertyName(property) + " IN(:" + _property + ")");
			params.put(_property, value);
		}
		return this;
	}

	public SqlBuilder notIn(String property, Object value) {
		if (value != null) {
			String _property = property + "_notin";
			criteria.add(getColumnNameByPropertyName(property) + " NOT IN(:" + _property + ")");
			params.put(_property, value);
		}
		return this;
	}

	public SqlBuilder isNotNull(String property) {
		criteria.add(getColumnNameByPropertyName(property) + " IS NOT NULL");
		return this;
	}

	public SqlBuilder isNull(String property) {
		criteria.add(getColumnNameByPropertyName(property) + " IS NULL");
		return this;
	}

	public Integer getPage() {
		return page;
	}

	public Integer getRows() {
		return rows;
	}

	public SqlBuilder setOrder(String order, String orderType) {
		this.order = order;
		this.orderType = orderType;
		return this;
	}

	public SqlBuilder setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	public SqlBuilder setPage(Integer page, Integer limit) {
		this.page = page;
		this.rows = limit;
		return this;
	}

	public String getOrderSql() {
		if (StringUtils.isNotEmpty(this.order)) {
			return " ORDER BY " + getColumnNameByPropertyName(this.order) + " " + this.orderType;
		} else {
			return null;
		}
	}

	public String getOrderBySql() {
		if (StringUtils.isNotEmpty(this.orderBy)) {
			return " ORDER BY " + this.orderBy + " ";
		} else {
			return null;
		}
	}

	public <T> String getTableName(Class<T> cls) {
		if (cls == null) {
			throw new SqlBuilderException("类不能为空!");
		}
		Table table = cls.getAnnotation(Table.class);
		if (table == null || table.value() == "") {
			throw new SqlBuilderException("没有table注解或者配置为空!");
		}
		return table.value();
	}

	public <T> String getFieldByClass(Class<T> cls) {
		List<String> fieldList = new ArrayList<String>();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			if ("serialVersionUID".equals(field.getName())) {
				continue;
			}
			Transient trans = field.getAnnotation(Transient.class);
			if (trans == null || !trans.isTransient()) {
				fieldList.add(getColumnNameByPropertyName(field.getName()));
			}
		}
		return StringUtils.join(fieldList, ",");
	}

	public <T> String getSelectFieldByClass(Class<T> cls) {
		List<String> fieldList = new ArrayList<String>();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			if ("serialVersionUID".equals(field.getName())) {
				continue;
			}
			Transient trans = field.getAnnotation(Transient.class);
			if (trans == null || !trans.isTransient()) {
				Column colName = field.getAnnotation(Column.class);
				if (null != colName) {
					fieldList.add(colName.value() + " as " + field.getName());
				} else {
					fieldList.add(underscoreName(field.getName()));
				}
			}
		}
		return StringUtils.join(fieldList, ",");
	}

	public <T> String getInsertFieldByClass(Class<T> cls) {
		List<String> fieldList = new ArrayList<String>();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			if ("serialVersionUID".equals(field.getName())) {
				continue;
			}
			Transient trans = field.getAnnotation(Transient.class);
			if (trans == null || !trans.isTransient()) {
				if (batchUpdate.booleanValue()) {
					fieldList.add(":" + field.getName());
					continue;
				}
				Id id = field.getAnnotation(Id.class);
				if (id != null) {
					Object fieldVal = params.get(getColumnNameByPropertyName(field.getName()));
					if (fieldVal instanceof String && StringUtils.isNotBlank(fieldVal.toString())) {
						fieldList.add(":" + field.getName());
					} else if (!(fieldVal instanceof String) && null != fieldVal) {
						fieldList.add(":" + field.getName());
					} else {
						GenerationType type = id.type();
						if (GenerationType.SEQUENCE == type) {
							String sequence = id.sequence();
							if (StringUtils.isEmpty(sequence)) {
								throw new SqlBuilderException("sequence为空!");
							}
							fieldList.add(sequence + ".NEXTVAL");
						} else if (GenerationType.UUID == type) {
							String uuid = UUID.randomUUID().toString();
							fieldList.add("'" + uuid + "'");
							setUuid(uuid);
						} else if (GenerationType.TIMEANDSEQUENCE == type) {
							String sequence = id.sequence();
							if (StringUtils.isEmpty(sequence)) {
								throw new SqlBuilderException("sequence为空!");
							}
							String dft = id.dateFormat().getDateFormatType(id.dateFormat());
							SimpleDateFormat sdf = new SimpleDateFormat(dft);
							String dateStr = sdf.format(new Date());
							String idStr = "";
							if (id.dbType() == DBType.ORACLE) {
								idStr = "'" + dateStr + "' || " + sequence + ".NEXTVAL";
							} else if (id.dbType() == DBType.MYSQL) {
								idStr = "concat('" + dateStr + "', " + sequence + ".NEXTVAL)";
							} else {
								idStr = "'" + dateStr + "' || " + sequence + ".NEXTVAL";
							}
							fieldList.add(idStr);
						} else {
							throw new SqlBuilderException("未知的增长类型!");
						}
					}
				} else {
					fieldList.add(":" + field.getName());
				}
			}
		}
		return StringUtils.join(fieldList, ",");
	}

	public <T> String getUpdateFieldByClass(Class<T> cls) {
		List<String> fieldList = new ArrayList<String>();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			if ("serialVersionUID".equals(field.getName())) {
				continue;
			}
			Transient trans = field.getAnnotation(Transient.class);
			if (trans == null || !trans.isTransient()) {
				Id id = field.getAnnotation(Id.class);
				if (id != null) {
					continue;
				} else {
					fieldList.add(getColumnNameByPropertyName(field.getName()) + "=:"
							+ getColumnNameByPropertyName(field.getName()));
				}
			}
		}
		return StringUtils.join(fieldList, ",");
	}

	protected String underscoreName(String name) {
		if (StringUtils.isEmpty(name)) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		result.append(lowerCaseName(name.substring(0, 1)));
		for (int i = 1; i < name.length(); i++) {
			String s = name.substring(i, i + 1);
			String slc = lowerCaseName(s);
			if (!s.equals(slc)) {
				result.append("_").append(slc);
			} else {
				result.append(s);
			}
		}
		return result.toString();
	}

	protected String lowerCaseName(String name) {
		return name.toLowerCase(Locale.US);
	}

	public String getWhereSql() {
		return StringUtils.join(criteria, " AND ");
	}

	public String getUpdateProperty() {
		return StringUtils.join(update_property, ",");
	}

	public String getSelectSql() {
		StringBuffer sql = new StringBuffer("SELECT ");
		String fileds = getSelectFieldByClass(cls);
		String tableName = getTableName(cls);
		String where = getWhereSql();
		String order = getOrderSql();
		String orderBy = getOrderBySql();
		Integer page = getPage();
		Integer rows = getRows();
		sql.append(fileds).append(" FROM ").append(tableName);
		if (StringUtils.isNotEmpty(where)) {
			sql.append(" WHERE ").append(where);
		}
		if (StringUtils.isNotEmpty(order)) {
			sql.append(" ").append(order);
		} else {
			if (StringUtils.isNotEmpty(orderBy)) {
				sql.append(" ").append(orderBy);
			}
		}
		if (null != page && null != rows) {
			// params.put("page", page);
			// params.put("rows", rows);
			// return PageUtils.assembleOraclePageSQLByName(sql.toString());
			return PageUtils.assemblePageSQL(sql.toString(), page.intValue(), rows.intValue(), this.dbType);
		} else {
			return sql.toString();
		}
	}

	public String getCountSql() {
		StringBuffer sql = new StringBuffer("SELECT COUNT(1) ");
		String tableName = getTableName(cls);
		String where = getWhereSql();
		sql.append(" FROM ").append(tableName);
		if (StringUtils.isNotEmpty(where)) {
			sql.append(" WHERE ").append(where);
		}
		return sql.toString();
	}

	public String getInsertSql() {
		StringBuffer sql = new StringBuffer("INSERT INTO ");
		String fileds = getFieldByClass(cls);
		String vauleFileds = getInsertFieldByClass(cls);
		String tableName = getTableName(cls);
		sql.append(tableName).append(" (").append(fileds).append(")  VALUES (");
		sql.append(vauleFileds).append(")");
		return sql.toString();
	}

	public String getUpdateSql() {
		StringBuffer sql = new StringBuffer("UPDATE ");
		String vauleFileds = getUpdateFieldByClass(cls);
		String tableName = getTableName(cls);
		String where = getWhereSql();
		sql.append(tableName).append(" SET ").append(vauleFileds).append(" WHERE ");
		sql.append(where);
		return sql.toString();
	}

	public String getUpdateByPropertySql() {
		StringBuffer sql = new StringBuffer("UPDATE ");
		String vauleFileds = getUpdateProperty();
		String tableName = getTableName(cls);
		String where = getWhereSql();
		sql.append(tableName).append(" SET ").append(vauleFileds).append(" WHERE ");
		sql.append(where);
		return sql.toString();
	}

	public String getDeleteSql() {
		StringBuffer sql = new StringBuffer("DELETE FROM ");
		String tableName = getTableName(cls);
		String where = getWhereSql();
		sql.append(tableName).append(" WHERE ");
		sql.append(where);
		return sql.toString();
	}

	public Class<?> getCls() {
		return this.cls;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public static void main(String[] args) {

	}

}
