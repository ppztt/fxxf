package net.mingsoft.fxxf.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * @author laijunbao
 * @Description 监督投诉列表
 * @createTime 2020-03-02 0002 18:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@ApiModel(value = "监督投诉")
public class FeedbackComplaintVo implements Serializable {
    /**
     * 留言反馈对象
     */
    @ApiModelProperty(value = "经营者ID")
    private Integer applicantsId;

    /**
     * 操作记录
     */
    @ApiModelProperty(value = "经营者注册名称")
    private String regName;

    /**
     * 操作记录
     */
    @ApiModelProperty(value = "留言数量")
    private Integer count;

    /**
     * 操作记录
     */
    @ApiModelProperty(value = "处理数量")
    private Integer handleCnt;

    /**
     * 是否新消息(0:不是；1:是)
     */
    @ApiModelProperty(value = "是否新消息(0:不是；1:是)")
    private int isNew;

    public Integer getHandleCnt() {
        return handleCnt == null ? 0 : handleCnt;
    }
}
