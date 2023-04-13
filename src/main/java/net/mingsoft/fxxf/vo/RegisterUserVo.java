package net.mingsoft.fxxf.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * @Author: yrg
 * @Date: 2020-09-20 10:21
 * @Description: TODO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserVo {

    @NotNull(message = "用户名必填")
    @ApiModelProperty(value = "用户名", required = true)
    private String account;

    @Email(message = "电子邮件格式不正确")
    @ApiModelProperty(value = "电子邮件", required = true)
    private String email;

    @NotNull(message = "验证码必填")
    @ApiModelProperty(value = "验证码", required = true)
    private String ecode;

    @NotNull(message = "密码必填")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @NotNull(message = "密码必填")
    @ApiModelProperty(value = "再次确认密码", required = true)
    private String confirmPassword;

}
