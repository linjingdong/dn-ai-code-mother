package com.dn.dnaicodemotherbackend.service.impl;

import com.dn.dnaicodemotherbackend.service.UserService;
import com.dn.dnaicodemotherbackend.mapper.UserMapper;
import com.dn.dnaicodemotherbackend.model.entity.User;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户 服务层实现。
 *
 * @author Lin_jd
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
