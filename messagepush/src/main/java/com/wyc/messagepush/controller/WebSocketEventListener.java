package com.wyc.messagepush.controller;

import com.wyc.messagepush.entity.MyPrincipal;
import com.wyc.messagepush.entity.SocketSessionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import java.util.Map;

/**
 * Created by rajeevkumarsingh on 25/07/17.
 */
@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    /**session操作类*/
    @Autowired
    SocketSessionRegistry webAgentSessionRegistry;
    //Spring WebSocket消息发送模板
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener( SessionConnectedEvent event) {
        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
        GenericMessage genericMessage = (GenericMessage) event.getMessage().getHeaders().get("simpConnectMessage");
        Map mapUser = (Map) genericMessage.getHeaders().get("simpSessionAttributes");
        String username = ((MyPrincipal)mapUser.get("user")).getName();
        logger.info("Successful Connection Establishment,Username is:"+username);
        webAgentSessionRegistry.registerSessionId(username,sessionId);
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
     //   String username = (String) headerAccessor.getSessionAttributes().get("user");
        String username = ((MyPrincipal) headerAccessor.getSessionAttributes().get("user")).getName();
        if(username != null) {
            // 连接断开时，将session从全局HashMap移除
            webAgentSessionRegistry.unregisterSessionId(username,sessionId);
            logger.info("User Disconnected : " + username);
/*
          // 如果是聊天室项目退出时可以发消息进行广播通知
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);

            messagingTemplate.convertAndSend("/topic/public", chatMessage);*/
        }
    }
}
