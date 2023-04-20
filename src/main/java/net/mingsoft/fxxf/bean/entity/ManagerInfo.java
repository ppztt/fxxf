package net.mingsoft.fxxf.bean.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author luwb
 * @date 2023-04-18
 */
@Data
@ApiModel(value = "ManagerInfo")
public class ManagerInfo {

    @ApiModelProperty(value = "与manager表id对应")
    private Integer id;

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
    private Byte usertype;

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

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
