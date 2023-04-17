package net.mingsoft.fxxf.bean.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author laijunbao
 * @Date: 2020-01-09-0009 12:50
 * @Description: 接口统一返回格式
 */
public class ApiResult<T> implements Serializable {

    /**
     * 请求成功状态码
     */
    private static final String CODE_SUCCESS = "200";

    /**
     * 请求失败状态码
     */
    private static final String CODE_FAIL = "500";

    /**
     * 请求成功信息
     */
    private static final String MSG_SUCCESS="请求成功";

    /**
     * 请求失败信息
     */
    private static final String MSG_FAIL="请求失败";

    /**
     * 状态码
     */
    @ApiModelProperty(value = "状态码")
    private String code;

    /**
     * 提示信息
     */
    @ApiModelProperty(value = "提示信息")
    private String msg;

    /**
     * 返回对象
     */
    @ApiModelProperty(value = "返回对象")
    public T data;


    public ApiResult(){
    }

    public ApiResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResult(String code, String msg,T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ApiResult success(){
        return new ApiResult(CODE_SUCCESS,MSG_SUCCESS);
    }

    public static ApiResult success(Object data){
        return new ApiResult(CODE_SUCCESS,MSG_SUCCESS, data);
    }

    public static ApiResult fail(){
        return new ApiResult(CODE_FAIL, MSG_FAIL);
    }

    public static ApiResult fail(String msg){
        return new ApiResult(CODE_FAIL, msg);
    }

    public static ApiResult fail(Object data){
        return new ApiResult(CODE_FAIL, MSG_FAIL,data);
    }

    public static ApiResult fail(String msg, Object data){
        return new ApiResult(CODE_FAIL, msg,data);
    }

    public static ApiResult fail(String code, String msg, Object data){
        return new ApiResult(code, msg, data);
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
