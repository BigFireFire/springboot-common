package com.itactic.core.model;

import com.alibaba.fastjson.JSONObject;

public class AjaxResultV2 {

	private int code;
	private String msg;
	private Object data;

	private AjaxResultV2(int code, String msg){
		this.code = code;
		this.msg = msg;
	}

	private AjaxResultV2(int code, String msg, Object data){
		this.code = code;
		this.msg = msg;
		this.data = data;
	}


	public static AjaxResult success(String msg){
		return new AjaxResult(0, msg);
	}
	
	public static AjaxResult success(String msg, Object data){
		return new AjaxResult(0, msg, data);
	}
	
	public static AjaxResult error(String msg){
		return new AjaxResult(-1, msg);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
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
