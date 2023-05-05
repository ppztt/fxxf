package net.mingsoft.fxxf.bean.vo;

import lombok.Data;

/**
 * @author luwb
 * @date 2023-05-05
 */
@Data
public class HandleResultStatisticsVo {

    /**
     * 经营者注册名称
     */
    private String regName;

    /**
     * 区域：所在市/区
     */
    private String area;

    /**
     * 处理结果为‘督促告诫’的次数
     */
    private Integer beSupervisedCnt;

    /**
     * 处理结果为‘摘牌’的次数
     */
    private Integer delCnt;

    /**
     * 处理结果为‘其他’的次数
     */
    private Integer otherCnt;

}
