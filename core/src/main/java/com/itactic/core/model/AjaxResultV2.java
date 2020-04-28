package com.itactic.core.model;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itactic.core.constants.BootConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class AjaxResultV2<T> {

	@ApiModelProperty("状态(0成功1错误2未登陆3刷新4重复提交)")
	private Integer status;
	@ApiModelProperty("错误信息")
	private String msg = "操作成功";
	@ApiModelProperty("数据对象")
	private T data;
	@ApiModelProperty(hidden = true)
	@JsonIgnore
	private transient Boolean ok;

	public AjaxResultV2() {

	}

	public AjaxResultV2(Integer status) {
		this.status = status;
	}

	public AjaxResultV2(Integer status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	public AjaxResultV2(T data) {
		this.data = data;
	}

	public AjaxResultV2(Integer status, String msg, T data) {
		this.status = status;
		this.msg = msg;
		this.data = data;
	}

	public static AjaxResultV2<String> ok() {
		return new AjaxResultV2<String>(BootConstants.AJAX_STATUS.success);
	}

	public static AjaxResultV2<String> ok(String msg) {
		return new AjaxResultV2<String>(BootConstants.AJAX_STATUS.success,"操作成功");
	}

	public static <E> AjaxResultV2<E> ok(String msg, E data) {
		AjaxResultV2<E> ajaxResult = new AjaxResultV2<>();
		ajaxResult.setMsg(msg);
		ajaxResult.setStatus(BootConstants.AJAX_STATUS.success);
		ajaxResult.setData(data);
		return ajaxResult;
	}

	public static <E> AjaxResultV2<E> ok(E data) {
		AjaxResultV2<E> ajaxResult = new AjaxResultV2<>();
		ajaxResult.setMsg("操作成功");
		ajaxResult.setData(data);
		ajaxResult.setStatus(BootConstants.AJAX_STATUS.success);
		return ajaxResult;
	}

	public static AjaxResultV2<String> error(String msg) {
		AjaxResultV2<String> ajaxResult = new AjaxResultV2<String>(BootConstants.AJAX_STATUS.error);
		ajaxResult.setMsg(msg);
		return ajaxResult;
	}

	public static <E> AjaxResultV2<E> error(E E, String msg) {
		AjaxResultV2<E> ajaxResult = new AjaxResultV2<E>(E);
		ajaxResult.setStatus(BootConstants.AJAX_STATUS.error);
		ajaxResult.setMsg(msg);
		ajaxResult.setData(E);
		return ajaxResult;
	}

	public static AjaxResultV2<String> noLogin() {
		AjaxResultV2<String> ajaxResult = new AjaxResultV2<String>(BootConstants.AJAX_STATUS.nologin);
		ajaxResult.setMsg("用户未登陆");
		return ajaxResult;
	}

	public static AjaxResultV2<String> isRepeat() {
		AjaxResultV2<String> ajaxResult = new AjaxResultV2<String>(BootConstants.AJAX_STATUS.repeat);
		ajaxResult.setMsg("用户重复提交");
		return ajaxResult;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public boolean isOk() {
		if (this.status == null) {
			this.ok = false;
		}
		this.ok = this.status.intValue() == BootConstants.AJAX_STATUS.success.intValue();
		return ok;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
