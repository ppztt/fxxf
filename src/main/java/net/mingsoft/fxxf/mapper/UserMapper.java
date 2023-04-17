package net.mingsoft.fxxf.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.mingsoft.fxxf.bean.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Ligy
 **/
@Repository
public interface UserMapper extends BaseMapper<User> {

    List<User> userList(@Param("keyword") String keyword, Page<User> user);

    List<String> existsAccount(String account);

    List<User> enterpriseList(@Param("keyword") String keyword, Page<User> user);

    List<User> industryAssociationList(@Param("keyword") String keyword, Page<User> user);
}