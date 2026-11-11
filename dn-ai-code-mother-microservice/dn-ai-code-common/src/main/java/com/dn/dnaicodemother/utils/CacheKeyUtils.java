package com.dn.dnaicodemother.utils;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;

/**
 * 缓存 KEY 工具类
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/6 02:20
 **/
public class CacheKeyUtils {
    public static String generateKey(Object obj) {
        if (obj == null) {
            return DigestUtil.md5Hex("null");
        }

        return DigestUtil.md5Hex(JSONUtil.toJsonStr(obj));
    }
}
