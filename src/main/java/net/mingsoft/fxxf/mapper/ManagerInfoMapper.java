package net.mingsoft.fxxf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.mingsoft.fxxf.bean.entity.ManagerInfo;
import net.mingsoft.fxxf.bean.vo.ManagerInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author luwb
 * @date 2023-04-18
 */
@Repository
public interface ManagerInfoMapper extends BaseMapper<ManagerInfo> {

    List<ManagerInfoVo> userList(@Param("keyword") String keyword, Page<ManagerInfoVo> managerInfoPage);

    List<String> existsAccount(String account);

    List<ManagerInfoVo> enterpriseList(@Param("keyword") String keyword, Page<ManagerInfoVo> managerInfoPage);

    List<ManagerInfoVo> industryAssociationList(@Param("keyword") String keyword, Page<ManagerInfoVo> managerInfoPage);

}
