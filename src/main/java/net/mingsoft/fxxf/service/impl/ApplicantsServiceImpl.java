package net.mingsoft.fxxf.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.mingsoft.fxxf.mapper.ApplicantsMapper;
import net.mingsoft.fxxf.entity.Applicants;
import net.mingsoft.fxxf.service.ApplicantsService;
import net.mingsoft.fxxf.vo.ApplicantsPageSearchVo;
import net.mingsoft.fxxf.vo.RegionVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 申报单位 服务实现类
 */
@Service
public class ApplicantsServiceImpl extends ServiceImpl<ApplicantsMapper, Applicants> implements ApplicantsService {

    @Resource
    private ApplicantsMapper applicantsMapper;

    @Override
    public List<RegionVo> getGdRegion() {
        return baseMapper.getGdRegion();
    }


    @Override
    public IPage<Applicants> listPage(ApplicantsPageSearchVo applicantsPageSearchVo) {
        return applicantsMapper.listPage(new Page<>(applicantsPageSearchVo.getCurrent(),applicantsPageSearchVo.getSize()),applicantsPageSearchVo);
    }

}
