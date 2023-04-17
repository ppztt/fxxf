package net.mingsoft.fxxf.bean.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author laijunbao
 */
@Data
public class OperatorStatisticsExcelVo implements Serializable {

    /**
     * 经营者注册名称
     */
    @Excel(name = "经营者注册名称")
    private String regName;

    /**
     * 统一社会信用代码
     */
    @Excel(name = "统一社会信用代码")
    private String creditCode;

    /**
     * 门店名称
     */
    @Excel(name = "门店名称")
    private String storeName;

    /**
     * 经营场所-详细地址
     */
    @Excel(name = "经营场所-详细地址")
    private String address;

    /**
     * 申报类别
     */
    @Excel(name = "申报类别")
    private String type;

    /**
     * 经营类别
     */
    @Excel(name = "经营类别")
    private String management;

    /**
     * 类别明细
     */
    @Excel(name = "类别明细")
    private String details;

    /**
     * 核实通过日期
     */
    @Excel(name = "核实通过日期")
    private String startTime;

    /**
     * 有效期
     */
    @Excel(name = "有效期")
    private String expirationDate;

    /**
     * 状态(1:在期； 0:摘牌)
     */
    @Excel(name = "状态")
    private String status;

    /**
     * 是否连续承诺
     */
    @Excel(name = "是否连续承诺")
    private String contCommitment;

}
