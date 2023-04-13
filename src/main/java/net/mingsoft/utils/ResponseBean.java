package net.mingsoft.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ResponseBean {

	/** http 状态码 **/
	private int code;

	/** 返回信息 **/
	private String msg;

	/** 返回的数据 **/
	private Object data;
	
	public ResponseBean() {
		super();
	}

	public ResponseBean(int code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}

	public ResponseBean(int code, String msg, Object data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
	
	public ResponseBean(Object data) {
		this.code = 200;
		this.msg = "请求成功";
		this.data = data;
	}

	public static ResponseBean result(ApiCode apiCode){
		return result(apiCode,null);
	}

	public static ResponseBean result(ApiCode apiCode,Object data){
		return result(apiCode,apiCode.getMsg(),data);
	}

	public static ResponseBean result(ApiCode apiCode,String msg,Object data){
		boolean success = false;
		if (apiCode.getCode() == ApiCode.SUCCESS.getCode()){
			success = true;
		}
		String message = apiCode.getMsg();
		if (StringUtils.isNotBlank(msg)){
			message = msg;
		}
		return new ResponseBean(apiCode.getCode(), msg, data);
	}

	public static ResponseBean ok(){
		return ok(null);
	}

	public static ResponseBean ok(Object data){
		return result(ApiCode.SUCCESS,data);
	}

	public static ResponseBean ok(Object data,String msg){
		return result(ApiCode.SUCCESS,msg,data);
	}

	public static ResponseBean okMap(String key,Object value){
		Map<String,Object> map = new HashMap<>();
		map.put(key,value);
		return ok(map);
	}

	public static ResponseBean fail(ApiCode apiCode){
		return result(apiCode,null);
	}

	public static ResponseBean fail(String msg){
		return result(ApiCode.FAIL,msg,null);

	}

	public static ResponseBean fail(ApiCode apiCode,Object data){
		if (ApiCode.SUCCESS == apiCode){
			throw new RuntimeException("失败结果状态码不能为" + ApiCode.SUCCESS.getCode());
		}
		return result(apiCode,data);

	}

	public static ResponseBean fail(String key,Object value){
		Map<String,Object> map = new HashMap<>();
		map.put(key,value);
		return result(ApiCode.FAIL,map);
	}

	public static ResponseBean fail() {
		return fail(ApiCode.FAIL);
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
}