package com.itactic.jdbc.jdbc.autocreate;

/**
 * @author 1Zx.
 * @date 2020/4/24 22:11
 */

import com.itactic.jdbc.constants.CommonConstants;
import com.itactic.jdbc.exception.SqlBuilderException;
import com.itactic.jdbc.jdbc.*;
import com.itactic.jdbc.jdbc.autocreate.annotation.AutoColumn;
import com.itactic.jdbc.jdbc.autocreate.enums.MySqlFieldType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 自动建表core
 * */
public class SearchPO {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Class<?> cls = null;

    /** 数据库字段名 */
    private List<String> fieldNameList = null;
    /** 字段长度 */
    private List<Integer> fieldLengthList = null;
    /** 字段能否为空 */
    private List<Boolean> fieldNullAbleList = null;
    /** 字段java类型 */
    private List<Class> fieldTypeList = null;
    /** 字段数据库类型 */
    private List<String> fieldDataBaseTypeList = null;
    /** 字段注释 */
    private List<String> fieldCommentList = null;

    private Set<Class> needLengthCls = new HashSet<>();
    private String primaryKeyField = null;
    private DbType dbType = null;
    private String dataSource;
    private boolean canCreate = true;
    private static boolean created = false;
    private Integer filedSize = 0;
    private SearchPO(Class<?> cls) {
        this.cls = cls;
        init();
        startCreate();
    }

    private void startCreate() {
        if (!canCreate) {
            /** 无法建表 */
        } else {
            StringBuffer sqlSB = new StringBuffer("create table ");
            sqlSB.append(getTableName(cls) + " ( ");
            switch (dbType) {
                case MYSQL:
                    sqlSB.append(createMySqlTableBody());
                    sqlSB.append(" ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
                break;
            }
        }
    }

    private String createMySqlTableBody() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < filedSize; i++) {
            sb.append(fieldNameList.get(i)).append(" ");
            if ("undefine".equals(fieldDataBaseTypeList.get(i))) {
                sb.append(MySqlFieldType.getDataBaseTypeByObject(fieldTypeList.get(i)));
            } else {
                sb.append(fieldDataBaseTypeList.get(i));
            }
        }
        return sb.toString();
    }

    public static boolean CreateTable(Class<?> cls) {
        new SearchPO(cls);
        return created;
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
            setDataSource(CommonConstants.DATASOURCE_TYPE.DEFAULT);
        } else {
            setDbType(dataSourceType.type());
            setDataSource(dataSourceType.value());
        }
        fieldNameList = new LinkedList<>();
        fieldTypeList = new LinkedList<>();
        fieldLengthList = new LinkedList<>();
        fieldNullAbleList = new LinkedList<>();
        fieldDataBaseTypeList = new LinkedList<>();
        Field[] fields = cls.getDeclaredFields();
        for (Field field: fields) {
            AutoColumn autoColumn = field.getAnnotation(AutoColumn.class);
            if (null == autoColumn) {
                continue;
            }
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            Transient trans = field.getAnnotation(Transient.class);
            if (null == trans || trans.isTransient()){
                continue;
            }
            Id id = field.getAnnotation(Id.class);
            if (null == id && StringUtils.isBlank(primaryKeyField)) {
                primaryKeyField = field.getName();
            }
            String fieldName = autoColumn.fieldName();
            Integer fieldLength = autoColumn.fieldLength();
            boolean fieldNullAble = autoColumn.fieldNullAble();
            if (StringUtils.isBlank(fieldName)) {
                fieldName = getTableFieldNameByClsFieldName(field.getName());
            }

            this.fieldNullAbleList.add(fieldNullAble);
            this.fieldLengthList.add(fieldLength);
            this.fieldNameList.add(fieldName);
            this.fieldTypeList.add(field.getType().getClass());
            this.fieldDataBaseTypeList.add(autoColumn.filedType());
            this.fieldCommentList.add(autoColumn.fieldComment());

            filedSize++;
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

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) {

    }
}
