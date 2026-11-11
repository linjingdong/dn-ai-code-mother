package com.dn.dnaicodeuser.service.impl;

import com.dn.dnaicodemother.innerservice.InnerUserService;
import com.dn.dnaicodemother.model.entity.User;
import com.dn.dnaicodemother.model.vo.UserVO;
import com.dn.dnaicodeuser.service.UserService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/11 22:58
 **/
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserService userService;

    @Override
    public List<User> listByIds(Collection<? extends Serializable> ids) {
        return userService.listByIds(ids);
    }

    @Override
    public User getById(Serializable id) {
        return userService.getById(id);
    }

    @Override
    public UserVO getUserVO(User user) {
        return userService.getUserVO(user);
    }
}
