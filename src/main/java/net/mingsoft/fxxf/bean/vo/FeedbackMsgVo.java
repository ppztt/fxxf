package net.mingsoft.fxxf.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackMsgVo {

    @ApiModelProperty(value = "放心消费承诺投诉总条数")
    private long type1Count;

    @ApiModelProperty(value = "放心消费承诺投诉未处理条数")
    private long type1NoHandlerCnt;

    @ApiModelProperty(value = "线下无理由退货承诺店投诉总条数")
    private long type2Count;

    @ApiModelProperty(value = "线下无理由退货承诺店投诉未处理条数")
    private long type2NoHandlerCnt;
}
