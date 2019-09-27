package com.wyc.messagepush.service;

import com.wyc.messagepush.entity.MyPrincipal;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by wyc
 * on 2019-9-16
 */
@Component
public class PrinalInterfaceFallbackFactory implements FallbackFactory<PrinalInterface> {
    @Override
    public PrinalInterface create(Throwable throwable) {
        return new PrinalInterface() {
            @Override
            public String member() {
                return null;
            }

            @Override
            public MyPrincipal test() {
                return null;
            }

            @Override
            public Map<String, Object> tokenInfo() {
                Map<String, Object> map=new HashMap<>();
                map.put("msg","服务超时");
                map.put("desc",throwable.getMessage());
                return map;
            }

            @Override
            public MyPrincipal memberPrincipal() {
                return null;
            }
        };
        }

}
