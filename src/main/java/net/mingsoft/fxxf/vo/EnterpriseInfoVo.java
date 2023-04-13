package net.mingsoft.fxxf.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 企业信息接口返回vo
 */
@Data
@ApiModel(value = "EnterpriseInfoVo", description = "企业信息")
public class EnterpriseInfoVo implements Serializable {

    @ApiModelProperty(value = "用户id")
    private Integer id;

    /**
     * 真实姓名（企业用户为：经营者注册名称）
     */
    @ApiModelProperty(value = "真实姓名（企业用户为：经营者注册名称）")
    private String realname;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

    /**
     * 门店名称
     */
    @ApiModelProperty(value = "门店名称")
    private String storeName;

    /**
     * 经营类别
     */
    @ApiModelProperty(value = "经营类别")
    private String management;

    /**
     * 负责人姓名
     */
    @ApiModelProperty(value = "负责人姓名")
    private String principal;

    /**
     * 负责人电话
     */
    @ApiModelProperty(value = "负责人电话")
    private String principalTel;

    /**
     * 所在市
     */
    @ApiModelProperty(value = "地市")
    private String city;

    /**
     * 所在区县
     */
    @ApiModelProperty(value = "区县")
    private String district;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    private String address;
}
