package net.mingsoft.fxxf.service;


import com.baomidou.mybatisplus.extension.service.IService;
import net.mingsoft.fxxf.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    List<User> userList(Integer current, Integer size, String keyword);

    List<String> existsAccount(String account);
}