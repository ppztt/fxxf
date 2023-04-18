package net.mingsoft.fxxf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import net.mingsoft.fxxf.bean.vo.ApplicantsExtend;

/**
 * @ClassName: IndexService
 * @Description 首页服务层
 * @Author Ligy
 * @Date 2020/1/14 15:00
 **/
public interface IndexService extends IService<ApplicantsExtend> {

    Page<ApplicantsExtend> searchList(Integer current, Integer size, String keyword, String type, String status);
}
