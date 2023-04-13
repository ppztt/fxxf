package net.mingsoft.fxxf.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.mingsoft.fxxf.mapper.UserMapper;
import net.mingsoft.fxxf.entity.User;
import net.mingsoft.fxxf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description 用户管理实现类
 **/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> userList(Integer current, Integer size, String keyword) {
        Page<User> page = new Page<>(current, size);
        List<User> userList = userMapper.userList(keyword, page);
        page.setRecords(userList);
        return userList;
    }

    @Override
    public List<String> existsAccount(String account) {
        return userMapper.existsAccount(account);
    }
}