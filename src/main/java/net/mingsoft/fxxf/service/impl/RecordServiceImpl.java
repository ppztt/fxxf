package net.mingsoft.fxxf.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.mingsoft.fxxf.mapper.RecordMapper;
import net.mingsoft.fxxf.bean.entity.Record;
import net.mingsoft.fxxf.service.RecordService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作记录 服务实现类
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {

}
