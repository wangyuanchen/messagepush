package com.wyc.messagepush.entity;

import java.security.Principal;
/**
 * 实现Principal接口用来接收Feign调用的OAuth2的Principal的值
 * 所有的序列化操作必须要有默认构造器，可以不写，这里做一下说明
 *
 */
public class MyPrincipal implements Principal {
    // 用户名
    private String username;

    public MyPrincipal(){
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getName() {
        return username;
    }
}
