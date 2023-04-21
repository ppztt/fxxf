package net.mingsoft.fxxf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.fxxf.mapper.ManagerMapper;
import net.mingsoft.fxxf.service.ManagerService;
import org.springframework.stereotype.Service;

/**
 * @author luwb
 * @date 2023-04-20
 */
@Service
public class ManagerServiceImpl extends ServiceImpl<ManagerMapper, ManagerEntity> implements ManagerService {
}