package com.dn.dnaicodemotherbackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/22 22:14
 **/

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 必须有的角色
     */
    String mustRole() default "";
}
