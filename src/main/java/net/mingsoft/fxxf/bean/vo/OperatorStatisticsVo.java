package net.mingsoft.fxxf.bean.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author laijunbao
 * @Description 经营者统计
 * @createTime 2020-03-02 0002 22:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class OperatorStatisticsVo implements Serializable {

    /**
     * 地区
     */
    @ApiModelProperty(value = "地区")
    @Excel(name = "地区")
    private String area;

    /**
     * 承诺单位总数
     */
    @ApiModelProperty(value = "承诺单位总数")
    @Excel(name = "承诺单位总数")
    private Integer applicantsCnt;

    /**
     * 退货期限超过七天的承诺店数量
     */
    @ApiModelProperty(value = "退货期限超过七天的承诺店数量")
    @Excel(name = "退货期限超过七天的承诺店数量")
    private Integer contents2Cnt;

    /**
     * 审核通过时间
     */
    @ApiModelProperty(value = "审核通过时间")
//    @Excel(name = "审核通过时间")
    private LocalDate startTime;

    /**
     * 增加其他承诺的单位数量
     */
    @ApiModelProperty(value = "增加其他承诺的单位数量")
    @Excel(name = "增加其他承诺的单位数量")
    private Integer addContents1Cnt;

    /**
     * 连续承诺单位数量
     */
    @ApiModelProperty(value = "连续承诺单位数量")
    @Excel(name = "连续承诺单位数量")
    private Integer commitmentCnt;

    /**
     * 零有效投诉单位数量
     */
    @ApiModelProperty(value = "零有效投诉单位数量")
    @Excel(name = "零有效投诉单位数量")
    private Integer nullCnt;

    /**
     * 被监督告诫单位数量
     */
    @ApiModelProperty(value = "被监督告诫单位数量")
    @Excel(name = "被监督告诫单位数量")
    private Integer beSupervisedCnt;

    /**
     * 被摘牌单位数量
     */
    @ApiModelProperty(value = "被摘牌单位数量")
    @Excel(name = "被摘牌单位数量")
    private Integer delCnt;

    @ApiModelProperty(value = "过渡期单位数量")
    @Excel(name = "过渡期单位数量")
    private Integer transitionPeriodCnt;

}
