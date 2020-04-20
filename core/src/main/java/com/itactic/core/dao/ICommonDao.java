package com.itactic.core.dao;



import com.itactic.core.model.PageBean;
import com.itactic.jdbc.jdbc.SqlBuilder;

import java.util.List;

/**
 * @author 1Zx.
 * @date 2020/4/20 11:32
 */
public interface ICommonDao {

    public Integer count(SqlBuilder sqlBuilder);

    public <T> List<T> query(SqlBuilder sqlBuilder);

    public <T> T queryForObject(SqlBuilder sqlBuilder);

    public void save(SqlBuilder sqlBuilder, Object obj);

    public Long saveReturnKey(SqlBuilder sqlBuilder, Object obj);

    public void save(SqlBuilder sqlBuilder, List<?> list);

    public void update(SqlBuilder sqlBuilder, Object obj);

    public void updateByProperty(SqlBuilder sqlBuilder);

    public void delete(SqlBuilder sqlBuilder);

    public Integer getSequence(String sequence_name);

    public <T> PageBean<T> queryByPage(SqlBuilder sqlBuilder);
}
