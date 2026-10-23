package com.dn.dnaicodemotherbackend.core.parser;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/23 21:58
 **/
public interface CodeParser<T> {
    /**
     * 解析代码内容
     * @param code 原始代码内容
     * @return 解析后的结果对象
     */
    T parseCode(String code);
}
