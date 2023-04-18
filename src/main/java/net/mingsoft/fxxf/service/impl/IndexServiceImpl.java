package net.mingsoft.fxxf.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import net.mingsoft.fxxf.bean.vo.ApplicantsExtend;
import net.mingsoft.fxxf.mapper.IndexMapper;
import net.mingsoft.fxxf.service.IndexService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName: IndexServiceImpl
 * @Description TODO
 * @Author Ligy
 * @Date 2020/1/14 15:00
 **/
@Service
public class IndexServiceImpl extends ServiceImpl<IndexMapper, ApplicantsExtend> implements IndexService {

    @Resource
    IndexMapper indexMapper;

    @Override
    public Page searchList(Integer current, Integer size, String keyword, String type, String status) {
        Page page = new Page(current, size);
        HashMap<String, Object> params = Maps.newHashMap();
        params.put("keyword", keyword);
        params.put("type", type);
        params.put("status", status);
        List<ApplicantsExtend> applicants = indexMapper.searchList(params, page);
        page.setRecords(applicants);
        return page;
    }
}

