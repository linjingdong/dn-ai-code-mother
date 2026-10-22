package com.dn.dnaicodemotherbackend.aop;

import com.dn.dnaicodemotherbackend.annotation.AuthCheck;
import com.dn.dnaicodemotherbackend.exception.BusinessException;
import com.dn.dnaicodemotherbackend.exception.ErrorCode;
import com.dn.dnaicodemotherbackend.model.entity.User;
import com.dn.dnaicodemotherbackend.model.enums.UserRoleEnum;
import com.dn.dnaicodemotherbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/22 22:15
 **/
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doIntercept(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取当前用户
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);

        String mustRole = authCheck.mustRole();
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        // 无需任何角色，直接放行
        if (mustRoleEnum == null) {
            joinPoint.proceed();
        }

        // 如果登录用户无权限，拦截
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 如果必须需要管理员，登录用户没有，拦截
        if (mustRoleEnum == UserRoleEnum.ADMIN && userRoleEnum != UserRoleEnum.ADMIN) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        return joinPoint.proceed();
    }
}
