package net.mingsoft.shiro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 登录参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("登录参数")
public class LoginUser {
    @NotBlank(message = "请输入账号")
    @ApiModelProperty("账号")
    private String username;

    @NotBlank(message = "请输入密码")
    @ApiModelProperty("密码")
    private String password;

    @NotBlank(message = "验证码Token不能为空")
    @ApiModelProperty("验证码Token")
    String verifyToken;

    @NotBlank(message = "请输入验证码")
    @ApiModelProperty("验证码")
    private String code;

}