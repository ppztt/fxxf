package net.mingsoft.fxxf.bean.entity;

import java.util.Date;

public class OperationLog {
	private Integer id;
	private String oper_modul;
	private String oper_type;
	private String oper_desc;
	private String oper_req_param;
	private String oper_resp_param;
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

	public String getOper_modul() {
		return oper_modul;
	}

	public void setOper_modul(String oper_modul) {
		this.oper_modul = oper_modul;
	}

	public String getOper_type() {
		return oper_type;
	}

	public void setOper_type(String oper_type) {
		this.oper_type = oper_type;
	}

	public String getOper_desc() {
		return oper_desc;
	}

	public void setOper_desc(String oper_desc) {
		this.oper_desc = oper_desc;
	}

	public String getOper_req_param() {
		return oper_req_param;
	}

	public void setOper_req_param(String oper_req_param) {
		this.oper_req_param = oper_req_param;
	}

	public String getOper_resp_param() {
		return oper_resp_param;
	}

	public void setOper_resp_param(String oper_resp_param) {
		this.oper_resp_param = oper_resp_param;
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

	@Override
	public String toString() {
		return "OperationLog{" +
				"id=" + id +
				", oper_modul='" + oper_modul + '\'' +
				", oper_type='" + oper_type + '\'' +
				", oper_desc='" + oper_desc + '\'' +
				", oper_req_param='" + oper_req_param + '\'' +
				", oper_resp_param='" + oper_resp_param + '\'' +
				", oper_user='" + oper_user + '\'' +
				", oper_method='" + oper_method + '\'' +
				", oper_uri='" + oper_uri + '\'' +
				", oper_ip='" + oper_ip + '\'' +
				", oper_time=" + oper_time +
				'}';
	}
}
