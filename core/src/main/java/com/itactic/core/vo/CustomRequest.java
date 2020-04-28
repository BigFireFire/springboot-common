package com.itactic.core.vo;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

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
	public String toString(){
		return JSONObject.toJSONString(this);
	}

}
