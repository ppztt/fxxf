package net.mingsoft.fxxf.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: FeedbadkUpload
 * @Description 留言反馈--附件上传实体类
 * @Author Ligy
 * @Date 2020/2/29 16:20
 **/
@Getter
@Setter
@ApiModel(value = "FeedbackUpload")
public class FeedbackUpload {

    @ApiModelProperty(value = "附件名称")
    String fileName;

    @ApiModelProperty(value = "附件存储路径")
    String path;
}
