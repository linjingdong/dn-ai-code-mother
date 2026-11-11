package com.dn.dnaicodemother.innerservice;

import com.dn.dnaicodemother.exception.BusinessException;
import com.dn.dnaicodemother.exception.ErrorCode;
import com.dn.dnaicodemother.model.entity.User;
import com.dn.dnaicodemother.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static com.dn.dnaicodemother.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户内部接口
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/11 17:20
 **/
public interface InnerUserService {

    List<User> listByIds(Collection<? extends Serializable> ids);

    User getById(Serializable id);

    UserVO getUserVO(User user);

    // 静态方法，避免跨服务调用
    static User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        return currentUser;
    }
}
