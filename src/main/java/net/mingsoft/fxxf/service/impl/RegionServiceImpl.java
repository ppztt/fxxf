package net.mingsoft.fxxf.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.mingsoft.fxxf.mapper.RegionMapper;
import net.mingsoft.fxxf.entity.Region;
import net.mingsoft.fxxf.service.RegionService;
import org.springframework.stereotype.Service;

@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {

}
