package net.mingsoft.fxxf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.mingsoft.fxxf.bean.entity.Type;
import net.mingsoft.fxxf.mapper.TypeMapper;
import net.mingsoft.fxxf.service.TypeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-17
 */
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {

}
