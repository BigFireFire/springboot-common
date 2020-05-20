package com.itactic.core.vo;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

public class CustomRequest {

	@ApiModelProperty(required = true, value = "json参数", name = "params")
	private Map<String, Object> params;
	@ApiModelProperty(required = false, value = "数据列表页数", name = "page", example = "1")
	private Integer page;
	@ApiModelProperty(required = false, value = "数据列表当前页显示数量", name = "limit", example = "10")
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
