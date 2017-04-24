package edu.sjsu.cmpe275.lab2.model;

public class JSONError {
	private String code;
	private String msg;
	
	public JSONError() {
	}
	
	public JSONError(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}

//public class BadRequestError extends JSONError
