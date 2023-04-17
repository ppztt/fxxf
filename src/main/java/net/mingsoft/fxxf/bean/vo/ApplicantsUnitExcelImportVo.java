package net.mingsoft.fxxf.bean.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.handler.inter.IExcelDataModel;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author laijunbao
 */
@Data
public class ApplicantsUnitExcelImportVo implements Serializable, IExcelModel, IExcelDataModel {

    private Integer rowNum;

    private String errorMsg;

    /**
     * 经营者注册名称
     */
    @Excel(name = "经营者注册名称")
    @NotBlank(message = "经营者注册名称不能为空")
    private String regName;

    /**
     * 统一社会信用代码
     */
    @Excel(name = "统一社会信用代码")
    @NotBlank(message = "统一社会信用代码不能为空")
    private String creditCode;

    /**
     * 门店名称
     */
    @Excel(name = "门店名称")
    @NotBlank(message = "门店名称不能为空")
    private String storeName;

    /**
     * 经营场所-所在市
     */
    @Excel(name = "经营场所-所在市")
    @NotBlank(message = "经营场所-所在市不能为空")
    private String city;

    /**
     * 经营场所-所在区县
     */
    @Excel(name = "经营场所-所在区县")
    @NotBlank(message = "经营场所-所在区县不能为空")
    private String district;

    /**
     * 经营场所-所在镇
     */
   // @Excel(name = "经营场所-所在镇")
   // @NotBlank(message = "经营场所-所在镇不能为空")
    private String town;

    /**
     * 经营场所-详细地址
     */
    @Excel(name = "经营场所-详细地址")
    @NotBlank(message = "经营场所-详细地址不能为空")
    private String address;

    /**
     * 网店名称
     */
    @Excel(name = "网店名称")
    @NotBlank(message = "网店名称不能为空")
    private String onlineName;

    /**
     * 所属平台
     */
    @Excel(name = "所属平台")
    @NotBlank(message = "所属平台不能为空")
    private String platform;

    /**
     * 网店链接
     */
    // @Excel(name = "网店链接")
    private String onlineLink;

    /**
     * 经营类别
     */
    @Excel(name = "经营类别")
    @NotBlank(message = "经营类别不能为空")
    private String management;

    /**
     * 类别明细
     */
    @NotBlank(message = "类别明细不能为空")
    @Excel(name = "类别明细")
    private String details;

    /**
     * 负责人姓名
     */
    @Excel(name = "负责人姓名")
    @NotBlank(message = "负责人姓名不能为空")
    private String principal;

    /**
     * 负责人电话
     */
    @Excel(name = "负责人电话")
    @NotBlank(message = "负责人电话不能为空")
    private String principalTel;

    /**
     * 联系人姓名
     */
    // @Excel(name = "联系人姓名")
    private String contacts;

    /**
     * 联系人电话
     */
    // @Excel(name = "联系人电话")
    private String contactsTel;

    /**
     * 加入的行业协会
     */
    // @Excel(name = "加入的行业协会")
    private String joinIndustry;

    /**
     * 是否连续承诺
     */
   // @Excel(name = "是否连续承诺")
    private String contCommitment;

    /**
     * 连续承诺次数
     */
   // @Excel(name = "连续承诺次数")
    private Integer commNum;

    /**
     * 品质保证或适用用品 承诺事项及内容
     */
    // @Excel(name = "品质保证-承诺事项及内容")
    private String contents1;

    /**
     * 诚信保证或退货期限 承诺事项及内容
     */
    // @Excel(name = "诚信保证-承诺事项及内容")
    private String contents2;

    /**
     * 维权保证或退货约定 承诺事项及内容
     */
    // @Excel(name = "维权保证-承诺事项及内容")
    private String contents3;

    /**
     * 其他承诺事项及具体内容
     */
    @Excel(name = "其他承诺事项及具体内容")
    @NotBlank(message = "其他承诺事项及具体内容不能为空")
    private String contents4;

    /**
     * 本单位声明内容
     */
    // @Excel(name = "本单位声明内容")
    // @NotBlank(message = "本单位声明内容不能为空")
    private String statement;

    /**
     * 本单位申请日期 --> 企业申请日期
     */
    @Excel(name = "企业申请日期",format = "yyyy-MM-dd")
    @NotNull(message = "企业申请日期不能为空")
    private Date applicationDate;

    /**
     * 行业协会意见内容
     */
    // @Excel(name = "行业协会意见内容")
    private String industryContent;

    /**
     * 行业协会意见日期
     */
    // @Excel(name = "行业协会意见日期",format = "yyyy-MM-dd")
    private Date industryDate;

    /**
     * 行业协会意见单位
     */
    // @Excel(name = "行业协会意见单位")
    private String industryCompanyName;

    /**
     * 行业协会意见审核人员
     */
    // @Excel(name = "行业协会意见审核人员")
    private String industryAuditors;

    /**
     * 消委会意见内容
     */
    // @Excel(name = "消委会意见内容")
    private String ccContent;

    /**
     * 消委会意见日期
     */
    // @Excel(name = "消委会意见日期",format = "yyyy-MM-dd")
    private Date ccDate;

    /**
     * 消委会意见单位
     */
    // @Excel(name = "消委会意见单位")
    private String ccCompanyName;

    /**
     * 消委会意见审核人员
     */
    // @Excel(name = "消委会意见审核人员")
    private String ccAuditors;

    /**
     * 核实通过日期
     */
    // @Excel(name = "核实通过日期",format = "yyyy-MM-dd")
    private Date auditDate;

    public void setCreditCode(String creditCode) {
        if(!Objects.isNull(creditCode)){
            this.creditCode = creditCode.trim();
        }
    }

    @Override
    public Integer getRowNum() {
        return this.rowNum;
    }

    @Override
    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum + 1;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
