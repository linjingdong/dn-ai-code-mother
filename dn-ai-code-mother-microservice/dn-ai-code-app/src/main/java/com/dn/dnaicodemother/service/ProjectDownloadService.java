package com.dn.dnaicodemother.service;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/31 10:29
 **/
public interface ProjectDownloadService {

    /**
     * 下载应用文件
     * @param projectPath 项目路径
     * @param downloadFileName 下载文件名称
     * @param response 响应头
     */
    void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response);
}
