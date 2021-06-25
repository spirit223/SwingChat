package com.javaStudyWay.swingChat;

import java.io.Serializable;

/**
 * 用于序列化保存聊天记录
 * 反序列化读取保存的聊天记录的消息类
 */
public class Message implements Serializable {
    public String message;

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                '}';
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message(String message) {
        this.message = message;
    }
}
