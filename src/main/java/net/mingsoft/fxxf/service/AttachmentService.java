package net.mingsoft.fxxf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.mingsoft.fxxf.bean.entity.Attachment;
import net.mingsoft.fxxf.bean.vo.ApiResult;
import net.mingsoft.utils.ResponseBean;

import java.util.List;

/**
 *
 * 资料管理 服务类
 *
 * @author Ligy
 * @date 2020-01-09
 */
public interface AttachmentService extends IService<Attachment> {

    ApiResult attachmentInfo(Attachment attachment);

    ResponseBean attachmentDel(List<Integer> idArr);

    ResponseBean saveUploadInfo(Attachment attachment);
}
