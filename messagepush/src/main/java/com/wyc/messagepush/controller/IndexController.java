package com.wyc.messagepush.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 页面Controller类
 */
@Controller
public class IndexController
{
    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello world!!";
    }

    @RequestMapping("/fllindex")
    public String getIndex(){
        return "1";
    }

    @RequestMapping("/fllindex2")
    public String getIndex2(){
        return "2";
    }
}
