package net.mingsoft.fxxf.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "AuditLogVo", description = "审核记录")
public class AuditLogVo implements Serializable {

    @ApiModelProperty(value = "审核单位")
    private String name;

    @ApiModelProperty(value = "审核结果")
    private String status;

    @ApiModelProperty(value = "审核时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "审核意见")
    private String contents;

}
