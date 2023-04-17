package net.mingsoft.fxxf.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @Author: yrg
 * @Date: 2020-01-15 10:15
 * @Description: 授权参数传递
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PermissionParmVo {

    @NotNull(message = "角色id不能为空")
    @ApiModelProperty(value = "角色id")
    private Integer roleid;

    @NotNull(message = "权限id不能为空")
    @ApiModelProperty(value = "权限id[]")
    private Integer[] permid;

}
