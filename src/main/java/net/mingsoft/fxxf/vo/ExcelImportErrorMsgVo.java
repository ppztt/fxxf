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
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "ExcelImportErrorMsgVo")
public class ExcelImportErrorMsgVo implements Serializable {
    @ApiModelProperty(value = "错误行数")
    private Integer rowNum;

    @ApiModelProperty(value = "错误信息")
    private String errorMsg;

    @ApiModelProperty(value = "文件id，用于确认上传")
    private String fileId;

    public ExcelImportErrorMsgVo(Integer rowNum, String errorMsg) {
        this.rowNum = rowNum;
        this.errorMsg = errorMsg;
    }

    public ExcelImportErrorMsgVo(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
