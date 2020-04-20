package com.itactic.core.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
public class PageBean<T> {

	@ApiModelProperty("记录数")
	private Integer total;

	@ApiModelProperty("对象数组")
	private List<T> rows;

	public PageBean() {

	}

	public PageBean(Integer total, List<T> rows) {
		this.total = total;
		this.rows = rows;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

}
