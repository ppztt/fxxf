package net.mingsoft.fxxf.bean.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author luwb
 * @date 2023-04-18
 */
@Data
@ApiModel(value = "ManagerInfoVo")
public class ManagerInfoVo implements Serializable {

    @ApiModelProperty(value = "manager表id")
    private String id;

    @ApiModelProperty(value = "角色编号")
    @JsonProperty("roleId")
    private Integer roleIds;

    @ApiModelProperty(value = "角色编号")
    private String roleName;

    @ApiModelProperty(value = "用户编号即商家编号")
    private Integer peopleId;

    @ApiModelProperty(value = "管理员标识，超级管理员：super，子管理员：open")
    private String managerAdmin;

    @ApiModelProperty(value = "管理员用户名")
    @JsonProperty("account")
    private String managerName;

    @ApiModelProperty(value = "管理员昵称")
    @JsonProperty("realname")
    private String managerNickName;

    @ApiModelProperty(value = "密码")
    @JsonProperty("password")
    private String managerPassword;

    @ApiModelProperty(value = "新密码")
    @JsonProperty("newPassword")
    private String newManagerPassword;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateDate;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @JsonProperty("createTime")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "删除标记")
    private Integer del;

    @ApiModelProperty(value = "锁定状态")
    private String managerLock;

    @ApiModelProperty(value = "电子邮件")
    private String email;

    @ApiModelProperty(value = "所在省")
    private String province;

    @ApiModelProperty(value = "所在市")
    private String city;

    @ApiModelProperty(value = "所在区县")
    private String district;

    @ApiModelProperty(value = "所在镇")
    private String town;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "邮政编码")
    private String zipcode;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "用户类型（1：后台用户； 2：企业用户；3：协会行业用户）")
    private Integer usertype;

    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

    @ApiModelProperty(value = "门店名称")
    private String storeName;

    @ApiModelProperty(value = "经营类别")
    private String management;

    @ApiModelProperty(value = "负责人姓名")
    private String principal;

    @ApiModelProperty(value = "负责人电话")
    private String principalTel;

}
