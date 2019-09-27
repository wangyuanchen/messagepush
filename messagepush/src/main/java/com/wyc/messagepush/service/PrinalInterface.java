package com.wyc.messagepush.service;

import com.wyc.messagepush.configure.FeignConfig;
import com.wyc.messagepush.entity.MyPrincipal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * Feign接口类，远程调用其他微服务，引入Feign配置，失败降级处理类
 */
@FeignClient(name = "eshop-auth",configuration={FeignConfig.class},fallbackFactory = PrinalInterfaceFallbackFactory.class)
public interface PrinalInterface {

    @GetMapping("/api/userinfo")
     String member();

    @GetMapping("/api/member")
    MyPrincipal test();

    @GetMapping("/user/tokeninfo")
    Map<String,Object> tokenInfo();

    @GetMapping("/user/member")
    MyPrincipal memberPrincipal();
}
