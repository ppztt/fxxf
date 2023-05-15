package net.mingsoft.fxxf.bean.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 经营者数据导出vo
 *
 * @author: huangjunjian
 */
@Data
public class ApplicantsExcelVo implements Serializable {


    /**
     * 经营者注册名称
     */
    @Excel(name = "经营者注册名称")
    @ExcelProperty(value = "经营者注册名称")
    private String regName;

    /**
     * 统一社会信用代码
     */
    @Excel(name = "统一社会信用代码")
    @ExcelProperty(value = "统一社会信用代码")
    private String creditCode;

    /**
     * 门店名称
     */
    @Excel(name = "门店名称")
    @ExcelProperty(value = "门店名称")
    private String storeName;

    /**
     * 经营场所-所在市
     */
    @Excel(name = "经营场所-所在市")
    @ExcelProperty(value = "经营场所-所在市")
    private String city;

    /**
     * 经营场所-所在区县
     */
    @Excel(name = "经营场所-所在区县")
    @ExcelProperty(value = "经营场所-所在区县")
    private String district;

    /**
     * 经营场所-所在镇
     */
    @Excel(name = "经营场所-所在镇")
    @ExcelProperty(value = "经营场所-所在镇")
    private String town;

    /**
     * 经营场所-详细地址
     */
    @Excel(name = "经营场所-详细地址")
    @ExcelProperty(value = "经营场所-详细地址")
    private String address;

    /**
     * 网店名称
     */
    @Excel(name = "网店名称")
    @ExcelProperty(value = "网店名称")
    private String onlineName;

    /**
     * 所属平台
     */
    @Excel(name = "所属平台")
    @ExcelProperty(value = "所属平台")
    private String platform;

    /**
     * 网店链接
     */
    @Excel(name = "网店链接")
    @ExcelProperty(value = "网店链接")
    private String onlineLink;

    /**
     * 经营类别
     */
    @Excel(name = "经营类别")
    @ExcelProperty(value = "经营类别")
    private String management;

    /**
     * 类别明细
     */
    @Excel(name = "类别明细")
    @ExcelProperty(value = "类别明细")
    private String details;

    /**
     * 负责人姓名
     */
    @Excel(name = "负责人姓名")
    @ExcelProperty(value = "负责人姓名")
    private String principal;

    /**
     * 负责人电话
     */
    @Excel(name = "负责人电话")
    @ExcelProperty(value = "负责人电话")
    private String principalTel;

    /**
     * 联系人姓名
     */
    @Excel(name = "联系人姓名")
    @ExcelProperty(value = "联系人姓名")
    private String contacts;

    /**
     * 联系人电话
     */
    @Excel(name = "联系人电话")
    @ExcelProperty(value = "联系人电话")
    private String contactsTel;

    /**
     * 加入的行业协会
     */
    @Excel(name = "加入的行业协会")
    @ExcelProperty(value = "加入的行业协会")
    private String joinIndustry;

    /**
     * 是否连续承诺
     */
    @Excel(name = "是否连续承诺")
    @ExcelProperty(value = "是否连续承诺")
    private String contCommitment;

    /**
     * 连续承诺次数
     */
    @Excel(name = "连续承诺次数")
    @ExcelProperty(value = "连续承诺次数")
    private Integer commNum;

    /**
     * 品质保证或适用用品 承诺事项及内容
     */
    @Excel(name = "品质保证-承诺事项及内容")
    @ExcelProperty(value = "品质保证-承诺事项及内容")
    private String contents1;

    /**
     * 诚信保证或退货期限 承诺事项及内容
     */
    @Excel(name = "诚信保证-承诺事项及内容")
    @ExcelProperty(value = "诚信保证-承诺事项及内容")
    private String contents2;

    /**
     * 维权保证或退货约定 承诺事项及内容
     */
    @Excel(name = "维权保证-承诺事项及内容")
    @ExcelProperty(value = "维权保证-承诺事项及内容")
    private String contents3;

    /**
     * 其他承诺事项及具体内容
     */
    @Excel(name = "其他承诺事项及具体内容")
    @ExcelProperty(value = "其他承诺事项及具体内容")
    private String contents4;

    /**
     * 本单位声明内容
     */
    @Excel(name = "本单位声明内容")
    @ExcelProperty(value = "本单位声明内容")
    private String statement;

    /**
     * 本单位申请日期
     */
    @Excel(name = "本单位申请日期")
    @ExcelProperty(value = "本单位申请日期")
    private String applicationDate;

    /**
     * 行业协会意见内容
     */
    @Excel(name = "行业协会意见内容")
    @ExcelProperty(value = "行业协会意见内容")
    private String industryContent;

    /**
     * 行业协会意见日期
     */
    @Excel(name = "行业协会意见日期")
    @ExcelProperty(value = "行业协会意见日期")
    private String industryDate;

    /**
     * 行业协会意见单位
     */
    @Excel(name = "行业协会意见单位")
    @ExcelProperty(value = "行业协会意见单位")
    private String industryCompanyName;

    /**
     * 行业协会意见审核人员
     */
    @Excel(name = "行业协会意见审核人员")
    @ExcelProperty(value = "行业协会意见审核人员")
    private String industryAuditors;

    /**
     * 消委会意见内容
     */
    @Excel(name = "消委会意见内容")
    @ExcelProperty(value = "消委会意见内容")
    private String ccContent;

    /**
     * 消委会意见日期
     */
    @Excel(name = "消委会意见日期")
    @ExcelProperty(value = "消委会意见日期")
    private String ccDate;

    /**
     * 消委会意见单位
     */
    @Excel(name = "消委会意见单位")
    @ExcelProperty(value = "消委会意见单位")
    private String ccCompanyName;

    /**
     * 消委会意见审核人员
     */
    @Excel(name = "消委会意见审核人员")
    @ExcelProperty(value = "消委会意见审核人员")
    private String ccAuditors;

    /**
     * 摘牌时间
     */
    @Excel(name = "摘牌时间")
    @ExcelProperty(value = "摘牌时间")
    private String delTime;

    /**
     * 摘牌具体原因
     */
    @Excel(name = "摘牌具体原因")
    @ExcelProperty(value = "摘牌具体原因")
    private String delReason;

    /**
     * 摘牌其它必要信息
     */
    @Excel(name = "摘牌其它必要信息")
    @ExcelProperty(value = "摘牌其它必要信息")
    private String delOther;

    /**
     * 核实通过日期
     */
    @Excel(name = "核实通过日期")
    @ExcelProperty(value = "核实通过日期")
    private String startTime;

    public void setCreditCode(String creditCode) {
        if (!Objects.isNull(creditCode)) {
            this.creditCode = creditCode.trim();
        }
    }
}
