package net.mingsoft.fxxf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.mingsoft.fxxf.bean.entity.Attachment;

import java.util.List;

/**
 * <p>
 * 资料管理 Mapper 接口
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
public interface AttachmentMapper extends BaseMapper<Attachment> {

    List<Attachment> attachmentInfo(Attachment attachment);

    void attachmentDel(List<Integer> idArr);

    void saveUploadInfo(Attachment attachment);

    List<Attachment> attachmentList(String keyword, Page<Attachment> page);
}
