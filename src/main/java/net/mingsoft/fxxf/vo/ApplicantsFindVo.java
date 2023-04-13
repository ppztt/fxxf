package net.mingsoft.fxxf.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author laijunbao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "ApplicantsFindVo")
public class ApplicantsFindVo implements Serializable {

    @ApiModelProperty(value = "是否重复（true：重复，false：没有重复）")
    private Boolean isRepeatRegName;
}
