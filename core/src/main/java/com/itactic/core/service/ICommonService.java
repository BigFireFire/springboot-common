package com.itactic.core.service;

import com.itactic.core.model.AjaxResult;

import java.util.LinkedHashMap;

/**
 * @author 1Zx.
 * @date 2021/1/21 16:45
 */
public interface ICommonService {

    <T> AjaxResult<T> save(LinkedHashMap entity, Class<?> cls);

    <T> AjaxResult<T> get(Class<?> cls, LinkedHashMap entity, Integer page, Integer limit);

    <T> AjaxResult<T> delete(LinkedHashMap entity, Class<?> aClass);

    <T> AjaxResult<T> update(LinkedHashMap entity, Class<?> aClass);
}
