package com.itactic.jdbc.jdbc.autocreate;

/**
 * @author 1Zx.
 * @date 2020/4/24 22:11
 */
import com.itactic.jdbc.constants.CommonConstants;
import com.itactic.jdbc.exception.SqlBuilderException;
import com.itactic.jdbc.jdbc.*;
import com.itactic.jdbc.jdbc.autocreate.annotation.AutoColumn;
import com.itactic.jdbc.jdbc.autocreate.config.AutoCreateTableConfig;
import com.itactic.jdbc.jdbc.autocreate.config.Constants;
import com.itactic.jdbc.utils.ScanSupport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.comparator.Comparators;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * 自动建表core
 * */
@Component
public final class SearchPO implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(SearchPO.class);
    private Class<?> cls = null;
    private DbType dbType = null;
    private static JdbcTemplate jdbcTemplate;
    private String dynamic = null;

    private SearchPO() {
    }

    private void SearchPO(Class<?> cls) {
        this.cls = cls;
        init();
        startCreate(cls.getAnnotation(Table.class));
    }

    private void SearchPO(Class<?> cls, String dynamic) {
        this.dynamic = dynamic;
        this.SearchPO(cls);
    }

    private void startCreate(Table table) {
        switch (dbType) {
            case MYSQL:
                StringBuffer sqlSB = new StringBuffer("create table ");
                String tableName = getTableName(cls);
                if (checkMYSQLTableExist(tableName)) {
                    /** 表已经存在了 */
                    if (Constants.AutoCreate.DELETEANDCREATE.equals(AutoCreateTableConfig.getTableBuildStrategy())) {
                        SearchPO.dropTable(tableName);
                    } else if (Constants.AutoCreate.NODROP.equals(AutoCreateTableConfig.getTableBuildStrategy())) {
                        /** 存在不创建 */
                        return;
                    }
                }
                sqlSB.append(tableName + " ( ");
                String mysqlTableBody = createMySqlTableBody();
                if (StringUtils.isBlank(mysqlTableBody)) {
                    throw new SqlBuilderException("生成建表语句主体为空！");
                }
                sqlSB.append(mysqlTableBody);
                if (StringUtils.isNotBlank(table.tableForeignKey())) {
                    if (!table.tableForeignKey().startsWith(",")) {
                        sqlSB.append(", ");
                    }
                    String foreignKey = table.tableForeignKey().replace(CommonConstants.DYNAMIC, this.dynamic);
                    if (foreignKey.endsWith(",")) {
                        foreignKey = foreignKey.substring(0, foreignKey.length() - 1);
                    }
                    sqlSB.append(foreignKey);
                }
                if (sqlSB.toString().endsWith(",")) {
                    sqlSB.deleteCharAt(sqlSB.length() - 1);
                }
                sqlSB.append(" ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
                logger.info("AutoCreateTableSql:[{}]", sqlSB.toString());
                try {
                    jdbcTemplate.execute(sqlSB.toString());
                } catch (Exception e) {
                    throw new SqlBuilderException(e.getMessage());
                }
                break;
            default:
                logger.error("----数据库类型不受支持----");
        }
    }

    private static boolean checkMYSQLTableExist(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return false;
        }
        Integer tableNum = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.TABLES T WHERE T.TABLE_NAME = ?",new Object[]{tableName},Integer.class);
        if (null != tableNum && !tableNum.equals(0)) {
            return true;
        }
        return false;
    }

    private String createMySqlTableBody() {
        StringBuffer sb = new StringBuffer();
        Field[] fields = cls.getDeclaredFields();
        boolean havePrimaryKey = false;
        for (Field field: fields) {
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            Transient trans = field.getAnnotation(Transient.class);
            if (null != trans){
                if (trans.isTransient()) {
                    continue;
                }
            }
            AutoColumn autoColumn = field.getAnnotation(AutoColumn.class);
            if (null == autoColumn) {
                continue;
            }
            Id id = field.getAnnotation(Id.class);
            StringBuffer fieldSql = new StringBuffer();
            fieldSql.append(" `");
            String fieldSqlName;
            if (StringUtils.isBlank(autoColumn.fieldName())) {
                fieldSqlName = getTableFieldNameByClsFieldName(field.getName());

            } else {
                fieldSqlName = field.getName();
            }
            fieldSql.append(fieldSqlName).append("` ").append(autoColumn.filedType()).append(" ");

            if (autoColumn.fieldNullAble() && null == id) {
                fieldSql.append("DEFAULT NULL ");
            } else {
                fieldSql.append("NOT NULL ");
            }

            if (StringUtils.isNotBlank(autoColumn.fieldComment())) {
                fieldSql.append("COMMENT '").append(autoColumn.fieldComment()).append("',");
            } else {
                fieldSql.append(",");
            }
            if (null != id && !havePrimaryKey) {
                fieldSql.append(" PRIMARY KEY (`").append(fieldSqlName).append("`),");
                havePrimaryKey = true;
            }
            sb.append(fieldSql);
        }
        if (-1 != sb.indexOf(",") && sb.toString().endsWith(",")) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private void init() {
        /** 初始化数据库类型 */
        DataSourceType dataSourceType = cls.getAnnotation(DataSourceType.class);
        if (null == dataSourceType) {
            setDbType(DbType.MYSQL);
        } else {
            setDbType(dataSourceType.type());
        }
    }

    private String getTableFieldNameByClsFieldName(String name) {
        StringBuffer sb = new StringBuffer();
        for (char c :name.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append('_').append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public <T> String getTableName(Class<T> cls) {
        if (cls == null) {
            throw new SqlBuilderException("类不能为空!");
        }
        Table table = cls.getAnnotation(Table.class);
        if (table == null || table.value() == "") {
            throw new SqlBuilderException("没有table注解或者配置为空!");
        }
        if (StringUtils.isNotBlank(dynamic)) {
            return table.value().replace(CommonConstants.DYNAMIC, dynamic);
        }
        return table.value();
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public void setDynamic(String dynamic) {
        this.dynamic = dynamic;
    }

    public String getDynamic() {
        return dynamic;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
        if (null == jdbcTemplate) {
            logger.error("----jdbcTemplate初始化失败----");
            throw new SqlBuilderException("自动建表工具初始化失败!");
        }
    }


    /**
     * 自动扫描path下的文件
     * @param path 文件路径
     * 跳过动态表
     * @see CommonConstants.DYNAMIC
     * */
    @Deprecated
    public static void CreateTable(String path) {
        Set<Class<?>> classSet = ScanSupport.getClass(path);
        if (null != classSet && classSet.size() > 0) {
            classSet.forEach(cls -> {
                Table table = cls.getAnnotation(Table.class);
                if (null != table && table.value().contains(CommonConstants.DYNAMIC)) {
                    new SearchPO().SearchPO(cls);
                }
            });
        }
    }

    /**
     * 动态表建表
     * */
    public static void CreateDynamicTable(Class<?> cls, String dynamic) {
        if (null == cls || StringUtils.isBlank(dynamic)) {
            throw new SqlBuilderException("实体类为空或动态表名为空!");
        }
        Table table = cls.getAnnotation(Table.class);
        if (null == table || !table.value().contains(CommonConstants.DYNAMIC)) {
            throw new SqlBuilderException("没有table注解或缺少动态表名标识");
        }
        new SearchPO().SearchPO(cls, dynamic);
    }

    /** 删除数据表 */
    public static void dropTable(String tableName) {
        if (StringUtils.isNotBlank(tableName)) {
            StringBuffer sb = new StringBuffer();
            sb.append("drop table ").append(tableName);
            try {
                jdbcTemplate.execute(sb.toString());
            } catch (Exception e) {
                logger.error("----dropTable执行出错：【{}】----", e.getMessage());
            }
        }
    }

    public static void dropTableByDynamic(Class<?> cls, String dynamic, DbType dbType) {
        if (StringUtils.isBlank(dynamic)) {
            logger.error("----动态名为空----");
            return;
        }
        Table table = cls.getAnnotation(Table.class);
        if (null == table) {
            logger.error("----缺少Table注解----");
            return;
        }
        String dyTableName = table.value();
        if (-1 == dyTableName.indexOf(CommonConstants.DYNAMIC)) {
            logger.error("----表名缺少动态标识----");
            return;
        }
        String tableName = dyTableName.replace(CommonConstants.DYNAMIC, dynamic);
        if (DbType.MYSQL == dbType) {
            dropTable(tableName);
        }

    }

    public static void main(String[] args) {

    }
}
