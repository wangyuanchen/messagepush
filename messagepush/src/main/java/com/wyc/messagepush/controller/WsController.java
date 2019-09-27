package com.wyc.messagepush.controller;

import com.wyc.messagepush.entity.SocketSessionRegistry;
import com.wyc.messagepush.entity.WsMessage;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.MessageHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Controller //注册一个Controller，WebSocket的消息处理需要放在Controller下
public class WsController {
    // 开启日志
    private static final Logger logger = LoggerFactory.getLogger(WsController.class);
    //Spring WebSocket消息发送模板
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    /**session操作类*/
    @Autowired
    private SocketSessionRegistry webAgentSessionRegistry;
    //发送广播通知

    @MessageMapping("/addNotice")   //接收客户端发来的消息，客户端发送消息地址为：/app/addNotice
    @SendTo("/topic/notice")        //向客户端发送广播消息（方式一），客户端订阅消息地址为：/topic/notice
    public WsMessage notice(String notice, Principal fromUser) {
        //TODO 业务处理
        WsMessage msg = new WsMessage();
     //   msg.setFromName(fromUser.getName());
        msg.setContent(notice);
        System.out.println("notice");
        //向客户端发送广播消息（方式二），客户端订阅消息地址为：/topic/notice
//        messagingTemplate.convertAndSend("/topic/notice", msg);
        return msg;
    }

    //发送点对点消息
    @MessageMapping("/msg")         //接收客户端发来的消息，客户端发送消息地址为：/app/msg
    @SendToUser("/queue/msg/result") //向当前发消息客户端（就是自己）发送消息的发送结果，客户端订阅消息地址为：/user/queue/msg/result
    public boolean sendMsg(WsMessage message, Principal fromUser){
        //TODO 业务处理
        message.setFromName(fromUser.getName());
        System.out.println(fromUser.getName());
        System.out.println("这个执行了吗");
        //向指定客户端发送消息，第一个参数Principal.name为前面websocket握手认证通过的用户name（全局唯一的），客户端订阅消息地址为：/user/queue/msg/new
        messagingTemplate.convertAndSendToUser(message.getToName(), "/queue/msg/new", message);
        System.out.println("执行啦");
        return true;
    }

    //广播推送消息
    // @Scheduled(fixedRate = 10000)
    //@SendTo("/topic/notice")
    public void sendTopicMessage() {
        System.out.println("后台广播推送！");
        WsMessage wsMessage=new WsMessage();
        wsMessage.setToName("oyzc");
        wsMessage.setContent("一百万");
        this.messagingTemplate.convertAndSend("/topic/notice",wsMessage);
    }

    /**
     * 同样的发送消息   只不过是ws版本  http请求不能访问
     * 根据用户key发送消息
     * @param
     * @return
     * @throws Exception
     */
    //  @MessageMapping("/msg/hellosingle")
    //  @RequestMapping("/msg/hellosingle")
    //@Scheduled(fixedRate = 5000)
    public void greeting2() throws Exception {
        Map<String,String> params = new HashMap(1);
        params.put("test","test");
        System.out.println("单点推送！");
        WsMessage message=new WsMessage();
        message.setToName("2");
        message.setContent("您有新消息待查看!");
        //这里没做校验
        String sessionId=webAgentSessionRegistry.getSessionIds(message.getToName()).stream().findFirst().get();
      //  String sessionId=webAgentSessionRegistry.getSessionIds(message.getToName());
        System.out.println("sessionId:"+sessionId);
        messagingTemplate.convertAndSendToUser(sessionId,"/queue/msg/new",message,createHeaders(sessionId));
    }


    @RequestMapping(value = "/socketmessage/inform", method = RequestMethod.POST)
    @ResponseBody
    public String messageInform(@RequestParam("messageJson")String messageJson) {
        Map<String,String> map = (Map) JSON.parse(messageJson);
        System.out.println("单点推送！");
        WsMessage message=new WsMessage();
       // message.setToName("1");
        message.setContent("1您有新消息待查看!");
        message.setCTaxNo(map.get("cTaxNo"));
        message.setIMachineNo(Integer.valueOf(map.get("iMachineNo")));
        ConcurrentMap<String, Set<String>> concurrentMap = webAgentSessionRegistry.getAllSessionIds();
        for (String key: concurrentMap.keySet()){
            if (key.toUpperCase().contains(message.getCTaxNo()))
            {
                //这里没做校验
                String sessionId=webAgentSessionRegistry.getSessionIds(key).stream().findFirst().get();
                //  String sessionId=webAgentSessionRegistry.getSessionIds(message.getToName());
                System.out.println("sessionId:"+sessionId);
                messagingTemplate.convertAndSendToUser(sessionId,"/queue/msg/new",message,createHeaders(sessionId));
            }

        }
        return "200";

    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
    /*@MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }*/

}