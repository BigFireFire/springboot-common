package com.itactic.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itactic.core.dao.ICommonDao;
import com.itactic.core.exception.BootCustomException;
import com.itactic.core.model.AjaxResult;
import com.itactic.core.service.IAdvicesService;
import com.itactic.core.service.ICommonService;
import com.itactic.jdbc.jdbc.SqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author 1Zx.
 * @date 2021/1/21 16:51
 */
@Service
@ConditionalOnProperty(prefix = "common.controller", name = "enable", havingValue = "true")
public class CommonServiceImpl implements ICommonService {

    @Resource
    private ICommonDao commonDao;

    private final Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Autowired(required = false)
    private IAdvicesService advicesService;

    @Override
    @Transactional
    public <T> AjaxResult<T> save(LinkedHashMap entity, Class<?> cls) {
        if (null == entity || null == cls) {
            return AjaxResult.error(null, "缺少必填参数");
        }
        Object o;
        try {
            o = create(cls, entity);
        } catch (IOException ioException) {
            logger.error("----LinkedHashMap转换实体失败：【{}】----", ioException.getMessage());
            throw new BootCustomException("系统错误");
        }
        if (null != advicesService) {
            logger.info(">>>>>执行addBefore环绕【{}】>>>>>", cls.getSimpleName());
            advicesService.addBefore(cls, o);
            logger.info("<<<<<addBefore环绕执行完毕【{}】<<<<<", cls.getSimpleName());
        }
        commonDao.save(SqlBuilder.build(cls), o);
        if (null != advicesService) {
            logger.info(">>>>>执行addAfter环绕【{}】>>>>>", cls.getSimpleName());
            advicesService.addAfter(cls, o);
            logger.info("<<<<<addAfter环绕执行完毕【{}】<<<<<", cls.getSimpleName());
        }
        return AjaxResult.ok("操作成功", null);
    }

    @Override
    public <T> AjaxResult<T> get(Class<?> cls, LinkedHashMap entity, Integer page, Integer limit) {
        if (null == entity || null == cls) {
            return AjaxResult.error(null, "缺少必填参数");
        }
        SqlBuilder sqlBuilder = SqlBuilder(cls, entity);
        sqlBuilder.setPage(page, limit);
        return AjaxResult.ok((T) commonDao.query(sqlBuilder));
    }

    @Override
    @Transactional
    public <T> AjaxResult<T> delete(LinkedHashMap entity, Class<?> cls) {
        if (null == entity || null == cls) {
            return AjaxResult.error(null, "缺少必填参数");
        }
        commonDao.delete(SqlBuilder(cls, entity));
        return AjaxResult.ok("操作成功", null);
    }

    @Override
    @Transactional
    public <T> AjaxResult<T> update(LinkedHashMap entity, Class<?> cls) {
        if (null == entity || null == cls) {
            return AjaxResult.error(null, "缺少必填参数");
        }
        commonDao.updateByProperty(SqlBuilder(cls, entity));
        return AjaxResult.ok("操作成功", null);
    }


    private Object create(Class<?> cls, LinkedHashMap entity) throws JsonProcessingException {
        return new ObjectMapper().readValue(JSON.toJSONString(entity), cls);
    }

    private SqlBuilder SqlBuilder (Class<?> cls, LinkedHashMap<String, Object> entity) {
        if (null == cls) {
            return null;
        }
        SqlBuilder sqlBuilder = SqlBuilder.build(cls);
        if (null != entity) {
            for (Map.Entry<String, Object> entry : entity.entrySet()) {
                String condition = entry.getKey();
                JSONObject conditionBody = JSONObject.parseObject(JSON.toJSONString(entry.getValue()));
                switch (condition) {
                    case "eq": conditionBody.forEach(sqlBuilder::eq); break;
                    case "neq": conditionBody.forEach(sqlBuilder::neq); break;
                    case "like": conditionBody.forEach(sqlBuilder::like); break;
                    case "lLike": conditionBody.forEach(sqlBuilder::lLike); break;
                    case "rLike": conditionBody.forEach(sqlBuilder::rLike); break;
                    case "lt": conditionBody.forEach(sqlBuilder::lt); break;
                    case "gt": conditionBody.forEach(sqlBuilder::gt); break;
                    case "lte": conditionBody.forEach(sqlBuilder::lte); break;
                    case "gte": conditionBody.forEach(sqlBuilder::gte); break;
                    case "update": conditionBody.forEach(sqlBuilder::update); break;
                    /** in和notIn条件的v为数组 */
                    case "in": conditionBody.forEach(sqlBuilder::in); break;
                    case "notIn": conditionBody.forEach(sqlBuilder::notIn); break;
                }
            }
        }
        return sqlBuilder;
    }
}
