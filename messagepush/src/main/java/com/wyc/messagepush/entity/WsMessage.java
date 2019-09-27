package com.wyc.messagepush.entity;

import lombok.Data;

/**
 * 消息实体类
 */
@Data
public class WsMessage {
    //消息接收人，对应认证用户Principal.name（全局唯一）
    private String toName;
    //消息发送人，对应认证用户Principal.name（全局唯一）
    private String fromName;
    //消息内容
    private Object content;
    // token
    private String token;
    // 税号
    private String cTaxNo;
    // 分机号
    private int  iMachineNo;

}
