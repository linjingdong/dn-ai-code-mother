package com.dn.dnaicodemotherbackend.controller;

import com.dn.dnaicodemotherbackend.common.BaseResponse;
import com.dn.dnaicodemotherbackend.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/22 05:16
 **/
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/")
    public BaseResponse<String> healthCheck() {
        return ResultUtils.success( "ok");
    }
}


