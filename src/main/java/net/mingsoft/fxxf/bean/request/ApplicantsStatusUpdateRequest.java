package net.mingsoft.fxxf.bean.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 根据经营者id更新状态及原因
 *
 * @author: huangjunjian
 */
@Data
public class ApplicantsStatusUpdateRequest implements Serializable {
    /**
     * 经营者id
     */
    @ApiModelProperty(value = "经营者id",required = true)

    private Integer applicantsId;

    /**
     * 状态变更原因
     */
    @ApiModelProperty(value = "状态变更原因")
    private String delReason;

    /**
     * 其他必要信息
     */
    @ApiModelProperty(value = "其他必要信息")
    private String delOther;

    /**
     * 审核状态 状态(1:在期； 0:摘牌；2:过期; 4:待审核; 5:县级审核通过; 6:市级审核通过; 7:审核不通过 )
     */
    @ApiModelProperty(value = "状态(0:摘牌；1:在期；",required = true)
    private Integer status;

}
