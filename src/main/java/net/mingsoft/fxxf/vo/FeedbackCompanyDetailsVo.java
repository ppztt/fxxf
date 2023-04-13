package net.mingsoft.fxxf.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author laijunbao
 * @Description 监督投诉-企业详情
 * @createTime 2020-03-02 0002 18:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FeedbackCompanyDetailsVo implements Serializable {

    /**
     * 问题类型
     */
    @ApiModelProperty(value = "留言id")
    private Integer id;

    /**
     * 问题类型
     */
    @ApiModelProperty(value = "问题类型")
    private String reason;

    /**
     * 反馈时间
     */
    @ApiModelProperty(value = "反馈时间")
    private LocalDateTime createTime;

    /**
     * 处理结果
     */
    @ApiModelProperty(value = "处理结果")
    private String result;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String status;

    /**
     * 是否新消息(0:不是；1:是)
     */
    @ApiModelProperty(value = "是否新消息(0:不是；1:是)")
    private int isNew;

    /*public String getStatus() {
        return "0".equalsIgnoreCase(status) ? "待处理" : "已处理";
    }*/
}
