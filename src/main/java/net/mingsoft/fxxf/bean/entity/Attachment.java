package net.mingsoft.fxxf.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 资料管理
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
@TableName("cc_attachment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@Setter
@Getter
public class Attachment extends Model<Attachment> implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 资料名称
     */
    private String filename;

    /**
     * 存储地址
     */
    private String storage;

    /**
     * 上传者
     */
    private String uploader;

    /**
     * 下载次数
     */
    private Integer downloads;

    /**
     * 创建时间(上传时间)
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public Attachment(String filename, String uploader) {
        this.filename = filename;
        this.uploader = uploader;
    }

    public Attachment(String filename, String storage, String uploader, Integer downloads, LocalDateTime createTime, LocalDateTime updateTime) {
        this.filename = filename;
        this.storage = storage;
        this.uploader = uploader;
        this.downloads = downloads;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
