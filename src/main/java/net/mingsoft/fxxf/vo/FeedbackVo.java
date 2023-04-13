package net.mingsoft.fxxf.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import net.mingsoft.fxxf.entity.Feedback;
import net.mingsoft.fxxf.entity.Record;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FeedbackVo implements Serializable {

    /**
     * 留言反馈对象
     */
    @ApiModelProperty(value = "留言反馈对象")
    private Feedback feedback;

    /**
     * 操作记录
     */
    @ApiModelProperty(value = "操作记录")
    private List<Record> history;

    /**
     * 状态(1:在期； 0:摘牌；2:过期)
     */
    @ApiModelProperty(value = "状态(1:在期； 0:摘牌；2:过期)")
    private int status;
}
