package com.dn.dnaicodemotherbackend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.dn.dnaicodemotherbackend.model.entity.App;
import com.dn.dnaicodemotherbackend.mapper.AppMapper;
import com.dn.dnaicodemotherbackend.service.AppService;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author Lin_jd
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{

}
