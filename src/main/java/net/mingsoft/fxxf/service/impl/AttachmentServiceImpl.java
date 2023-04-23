package net.mingsoft.fxxf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.bean.base.BaseResult;
import net.mingsoft.fxxf.bean.entity.Attachment;
import net.mingsoft.fxxf.mapper.AttachmentMapper;
import net.mingsoft.fxxf.service.AttachmentService;
import net.mingsoft.utils.ResponseBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 资料管理 服务实现类
 */
@Service
@Slf4j
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements AttachmentService {

    @Resource
    AttachmentMapper attachmentMapper;

    @Override
    public BaseResult attachmentInfo(Attachment attachment) {
        //TODO 分页查询
        ArrayList<Attachment> attachments;
        try {
            attachments = attachmentMapper.attachmentInfo(attachment);
            return BaseResult.success(attachments);
        } catch (Exception e) {
            log.error("查询资料信息发生异常：", e);
            return BaseResult.fail();
        }
    }

    @Override
    public ResponseBean saveUploadInfo(Attachment attachment) {
        try {
            attachmentMapper.saveUploadInfo(attachment);
        } catch (Exception e) {
            log.error("附件上传信息保存失败", e);
            return new ResponseBean(500, "附件上传信息保存失败");
        }
        return new ResponseBean(200, "附件上传信息保存成功");
    }

    @Override
    public ResponseBean attachmentDel(List<Integer> idArr) {
        try {
            attachmentMapper.attachmentDel(idArr);
        } catch (Exception e) {
            log.error("删除附件失败：", e);
            return new ResponseBean(500, "删除附件失败");
        }
        return new ResponseBean(200, "删除成功");
    }
}
