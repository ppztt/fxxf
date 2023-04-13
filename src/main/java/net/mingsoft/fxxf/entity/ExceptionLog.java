package net.mingsoft.fxxf.entity;

import java.util.Date;

public class ExceptionLog {
	private Integer id;
	private String exception_req_param;
	private String exception_name;
	private String exception_msg;
	private String oper_user;
	private String oper_method;
	private String oper_uri;
	private String oper_ip;
	private Date oper_time;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getException_req_param() {
		return exception_req_param;
	}

	public void setException_req_param(String exception_req_param) {
		this.exception_req_param = exception_req_param;
	}

	public String getException_name() {
		return exception_name;
	}

	public void setException_name(String exception_name) {
		this.exception_name = exception_name;
	}

	public String getException_msg() {
		return exception_msg;
	}

	public void setException_msg(String exception_msg) {
		this.exception_msg = exception_msg;
	}

	public String getOper_user() {
		return oper_user;
	}

	public void setOper_user(String oper_user) {
		this.oper_user = oper_user;
	}

	public String getOper_method() {
		return oper_method;
	}

	public void setOper_method(String oper_method) {
		this.oper_method = oper_method;
	}

	public String getOper_uri() {
		return oper_uri;
	}

	public void setOper_uri(String oper_uri) {
		this.oper_uri = oper_uri;
	}

	public String getOper_ip() {
		return oper_ip;
	}

	public void setOper_ip(String oper_ip) {
		this.oper_ip = oper_ip;
	}

	public Date getOper_time() {
		return oper_time;
	}

	public void setOper_time(Date oper_time) {
		this.oper_time = oper_time;
	}
}
