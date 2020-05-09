package com.itactic.core.model;

import com.alibaba.fastjson.JSONObject;
import com.itactic.core.constants.BootConstants;

public class AjaxResultV2 {

	private int status;
	private String msg;
	private Object data;

	private AjaxResultV2(int status, String msg){
		this.status = status;
		this.msg = msg;
	}

	private AjaxResultV2(int status, String msg, Object data){
		this.status = status;
		this.msg = msg;
		this.data = data;
	}


	public static AjaxResultV2 success(String msg){
		return new AjaxResultV2(BootConstants.AJAX_STATUS.success, msg);
	}
	
	public static AjaxResultV2 success(String msg, Object data){
		return new AjaxResultV2(BootConstants.AJAX_STATUS.success, msg, data);
	}

	public static AjaxResultV2 success(Object data) {
		return new AjaxResultV2(BootConstants.AJAX_STATUS.success,"操作成功",data);
	}
	
	public static AjaxResultV2 error(String msg){
		return new AjaxResultV2(BootConstants.AJAX_STATUS.error, msg);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	@Override
	public String toString(){
		return JSONObject.toJSONString(this);
	}

}
