package com.xiantao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiantao.dto.LoginDTO;
import com.xiantao.dto.RegisterDTO;
import com.xiantao.dto.UserUpdateDTO;
import com.xiantao.entity.User;
import com.xiantao.vo.UserVO;

public interface UserService extends IService<User> {

    UserVO register(RegisterDTO dto);

    UserVO login(LoginDTO dto);

    UserVO getUserInfo(Long userId);

    UserVO updateUserInfo(Long userId, UserUpdateDTO dto);

    boolean isAdmin(Long userId);
}
