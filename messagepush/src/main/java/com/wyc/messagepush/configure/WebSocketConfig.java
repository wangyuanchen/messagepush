package com.wyc.messagepush.configure;

import com.wyc.messagepush.entity.MyPrincipal;
import com.wyc.messagepush.service.PrinalInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import java.util.Map;
import java.security.Principal;

@Configuration  //注册为 Spring 配置类
/*
 * 开启使用STOMP协议来传输基于代理（message broker）的消息
 * 启用后控制器支持@MessgeMapping注解
 */
@EnableWebSocketMessageBroker
//继承 AbstractWebSocketMessageBrokerConfigurer 的配置类实现 WebSocket 配置或实现WebSocketMessageBrokerConfigurer接口
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
   // feign实例调用用户中心获取Principal或税号等信息对象
    @Autowired
    private PrinalInterface prinalInterface;
    //注册STOMP协议节点并映射url
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket") //注册一个 /websocket 的 websocket 节点
                .addInterceptors(myHandshakeInterceptor())  //添加 websocket握手拦截器
                .setHandshakeHandler(myDefaultHandshakeHandler())   //添加 websocket握手处理器
                .setAllowedOrigins("*") //设置允许可跨域的域名
                .withSockJS();  //指定使用SockJS协议
    }

    /**
     * WebSocket 握手拦截器
     * 可做一些用户认证拦截处理
     */
    private HandshakeInterceptor myHandshakeInterceptor(){
        return new HandshakeInterceptor() {
            /**
             * websocket握手连接
             * @return 返回是否同意握手
             */
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                ServletServerHttpRequest req = (ServletServerHttpRequest) request;
                //通过url的query参数获取认证参数
                String token = req.getServletRequest().getParameter("token");
                //根据token认证用户并拿到用户信息，不通过返回拒绝握手
                Principal user = authenticate();
                if(user == null){
                    logger.info("Authentication is failed!!! Connection rejection.");
                    return false;
                }
                logger.info("Authentication is Ok,Saving Authenticated Users,Username is "+user.getName());
                //保存认证用户
                attributes.put("token", token);
                attributes.put("user", user);
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

            }
        };
    }

    //WebSocket 握手处理器
    private DefaultHandshakeHandler myDefaultHandshakeHandler(){
        return new DefaultHandshakeHandler(){
            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                //设置认证通过的用户到当前会话中
                return (Principal)attributes.get("user");
            }
        };
    }

    /**
     * 定义一些消息连接规范（也可不设置）
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //设置客户端接收消息地址的前缀（可不设置）
        registry.enableSimpleBroker(
                "/topic",   //广播消息前缀
                "/queue"    //点对点消息前缀
        );
        //设置客户端接收点对点消息地址的前缀，默认为 /user
        registry.setUserDestinationPrefix("/user");
        //设置客户端向服务器发送消息的地址前缀（可不设置）
        registry.setApplicationDestinationPrefixes("/app");
        //   Use this for enabling a Full featured broker like RabbitMQ

        /*生产环境相关配置信息
        registry.enableStompBrokerRelay("/topic")
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest");
        */
    }

    /**
     * 根据token认证授权
     * @param token
     */
    private Principal authenticate(){
        //TODO 实现用户的认证并返回用户信息，如果认证失败返回 null
        // 一种：用户信息需继承 Principal 并实现 getName() 方法，返回全局唯一值
        // 二种：这里实现的是用token换取用户信息
        String username =  prinalInterface.member();
        MyPrincipal principal =  prinalInterface.test();
        if (principal == null)
        {
            logger.error("Failed to invoke authentication service！");
            return null;
        }
        principal.setUsername("12345678900"+username);
        System.out.println(principal.toString());
        System.out.println(principal.getName());
        logger.info("FeignClient is succeed,Username is:"+username);
        if(principal != null)
            return principal;
        return null;
    }


}
