package com.itactic.core.utils;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * @author 1Zx.
 * @date 2020/4/20 13:40
 * 公共接参类
 */
public class CustomRequest {

    private Map<String, Object> params;
    private Integer page;
    private Integer limit;

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
