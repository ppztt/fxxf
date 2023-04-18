package net.mingsoft.fxxf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.mingsoft.fxxf.bean.vo.ApplicantsExtend;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 申报单位 Mapper 接口
 * </p>
 *
 * @author Ligy
 * @since 2020-01-14
 */
public interface IndexMapper extends BaseMapper<ApplicantsExtend> {

    List<ApplicantsExtend> searchList(HashMap<String, Object> params, Page page);

}
