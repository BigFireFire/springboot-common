package com.itactic.core.dao.impl;

import com.itactic.core.dao.ICommonDao;
import com.itactic.core.model.PageBean;
import com.itactic.jdbc.jdbc.SqlBuilder;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 1Zx.
 * @date 2020/4/20 11:33
 */
@Repository("bootCommonDao")
public class CommonDaoImpl extends BaseDao implements ICommonDao {

    public Integer count(SqlBuilder sqlBuilder) {
        return getNamedTemplate().queryForObject(sqlBuilder.getCountSql(), sqlBuilder.getParams(), Integer.class);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> query(SqlBuilder sqlBuilder) {
        return (List<T>) getNamedTemplate().query(sqlBuilder.getSelectSql(), sqlBuilder.getParams(),
                BeanPropertyRowMapper.newInstance(sqlBuilder.getCls()));
    }

    public void save(SqlBuilder sqlBuilder, Object obj) {
        sqlBuilder.addParams(obj);
        getNamedTemplate().update(sqlBuilder.getInsertSql(), new BeanPropertySqlParameterSource(obj));
    }

    public Long saveReturnKey(SqlBuilder sqlBuilder, Object obj) {
        sqlBuilder.addParams(obj);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedTemplate().update(sqlBuilder.getInsertSql(), new BeanPropertySqlParameterSource(obj), keyHolder,
                sqlBuilder.getPks());
        return keyHolder.getKey().longValue();
    }

    public void delete(SqlBuilder sqlBuilder) {
        getNamedTemplate().update(sqlBuilder.getDeleteSql(), sqlBuilder.getParams());
    }

    public void save(SqlBuilder sqlBuilder, List<?> list) {
        sqlBuilder.setBatchUpdate(true);
        getNamedTemplate().batchUpdate(sqlBuilder.getInsertSql(), SqlParameterSourceUtils.createBatch(list.toArray()));
    }

    public void update(SqlBuilder sqlBuilder, Object obj) {
        sqlBuilder.addParams(obj);
        getNamedTemplate().update(sqlBuilder.getUpdateSql(), sqlBuilder.getParams());
    }

    public void updateByProperty(SqlBuilder sqlBuilder) {
        getNamedTemplate().update(sqlBuilder.getUpdateByPropertySql(), sqlBuilder.getParams());
    }

    @SuppressWarnings("unchecked")
    public <T> T queryForObject(SqlBuilder sqlBuilder) {
        List<T> list = (List<T>) getNamedTemplate().query(sqlBuilder.getSelectSql(), sqlBuilder.getParams(),
                BeanPropertyRowMapper.newInstance(sqlBuilder.getCls()));
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public Integer getSequence(String sequence_name) {
        String sql = "select " + sequence_name + ".Nextval from dual";
        return getNamedTemplate().queryForObject(sql, new HashMap<String, Object>(), Integer.class);
    }

    @Override
    public <T> PageBean<T> queryByPage(SqlBuilder sqlBuilder) {
        Integer total = this.count(sqlBuilder);
        List<T> rows = null;
        if (total.intValue() > 0) {
            if (sqlBuilder.getPage() == null || sqlBuilder.getRows() == null) {
                sqlBuilder.setPage(1, 10);
            }
            rows = this.query(sqlBuilder);
        }
        return new PageBean<T>(total, rows);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> queryBySql(Class<T> cls, String sql, List<Object> params) {
        return getJdbcTemplate().query(sql, params.toArray(), BeanPropertyRowMapper.newInstance(cls));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> queryBySql(Class<T> cls, String sql, Map<String, Object> params) {
        return getNamedTemplate().query(sql, params, BeanPropertyRowMapper.newInstance(cls));
    }

    @Override
    public <T> T queryForObjectBySql(Class<T> cls, String sql, List<Object> params) {
        List<T> list = queryBySql(cls, sql, params);
        if (null != list && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public <T> T queryForObjectBySql(Class<T> cls, String sql, Map<String, Object> params) {
        List<T> list = queryBySql(cls, sql, params);
        if (null != list && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
