package com.itactic.jdbc.jdbc.autocreate;

/**
 * @author 1Zx.
 * @date 2020/4/24 22:11
 */
import com.itactic.jdbc.exception.SqlBuilderException;
import com.itactic.jdbc.jdbc.*;
import com.itactic.jdbc.jdbc.autocreate.annotation.AutoColumn;
import com.itactic.jdbc.jdbc.autocreate.config.AutoCreateTableConfig;
import com.itactic.jdbc.jdbc.autocreate.config.Constants;
import com.itactic.jdbc.utils.ScanSupport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * 自动建表core
 * */
public class SearchPO {

    private static Logger logger = LoggerFactory.getLogger(SearchPO.class);
    private Class<?> cls = null;
    private DbType dbType = null;
    private boolean canCreate = true;
    private ConfigurableApplicationContext context;
    public static JdbcTemplate jdbcTemplate;

    private SearchPO(Class<?> cls) {
        this.cls = cls;
        init();
        startCreate();
    }

    private void startCreate() {
        if (canCreate) {
            switch (dbType) {
                case MYSQL:
                    StringBuffer sqlSB = new StringBuffer("create table `");
                    String tableName = getTableName(cls);
                    int tableNum = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.TABLES WHERE table_name =?",new Object[]{tableName},Integer.class);
                    if (Constants.AutoCreate.DELETEANDCREATE.equals(AutoCreateTableConfig.getTableBuildStrategy()) && tableNum > 0) {
                        /** 删除并创建 */
                        jdbcTemplate.execute("drop table `" + tableName + "`;");
                        logger.info("----删除表：[{}]成功----",tableName);
                    } else if (Constants.AutoCreate.NODROP.equals(AutoCreateTableConfig.getTableBuildStrategy())) {
                        /** 存在不创建 */
                        if (tableNum > 0) {
                            logger.info("Table:[{}] Already Exists",tableName);
                            return;
                        }
                    }
                    sqlSB.append(tableName + "` ( ");
                    sqlSB.append(createMySqlTableBody());
                    sqlSB.append(" ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
                    logger.info("AutoCreateTableSql:[{}]", sqlSB.toString());
                    jdbcTemplate.execute(sqlSB.toString());
                break;
            }
        }
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
            StringBuffer fieldSql = new StringBuffer();
            fieldSql.append(" `");
            String fieldSqlName;
            if (StringUtils.isBlank(autoColumn.fieldName())) {
                fieldSqlName = getTableFieldNameByClsFieldName(field.getName());

            } else {
                fieldSqlName = field.getName();
            }
            fieldSql.append(fieldSqlName).append("` ").append(autoColumn.filedType()).append(" ");

            if (autoColumn.fieldNullAble()) {
                fieldSql.append("DEFAULT NULL ");
            } else {
                fieldSql.append("NOT NULL ");
            }

            if (StringUtils.isNotBlank(autoColumn.fieldComment())) {
                fieldSql.append("COMMENT '").append(autoColumn.fieldComment()).append("',");
            } else {
                fieldSql.append(",");
            }
            Id id = field.getAnnotation(Id.class);
            if (null != id && !havePrimaryKey) {
                fieldSql.append(" PRIMARY KEY (`").append(fieldSqlName).append("`),");
                havePrimaryKey = true;
            }
            sb.append(fieldSql);
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    /** 入口 */
    public static void CreateTable(String path,ConfigurableApplicationContext context) {
        if (null == context) {
            logger.error("----context error----");
            return;
        }
        jdbcTemplate = context.getBeanFactory().getBean(JdbcTemplate.class);
        Set<Class<?>> classSet = ScanSupport.getClass(path);
        classSet.forEach(cls ->{
            Table table = cls.getAnnotation(Table.class);
            if (null != table) {
                new SearchPO(cls);
            }
        });
    }

    private void init() {
        if (null == cls || null == cls.getAnnotation(Table.class)) {
            logger.error("-----获取需要初始化实体类失败,或缺少@Table注解---");
            canCreate = false;
        }

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
        return table.value();
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

}
