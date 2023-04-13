package net.mingsoft.fxxf.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class BasePageVo implements Serializable {

    private static final long serialVersionUID = 7079369577943312861L;
    /**
     * 页码 第几页
     */
    @ApiModelProperty("页码 第几页")
    private Integer current = 1;

    /**
     * 每页展示数量
     */
    @ApiModelProperty("每页展示数量")
    private Integer size = 10;

}
