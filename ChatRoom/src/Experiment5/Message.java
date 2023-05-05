//该类用于封装用户发送的信息
package Experiment5;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String senderName;//发送者名称
    private String receiverName;//接收者名称
    private String messageContent;//消息内容
    private String sendTime;//发送时间
    private String messageType;//消息类型

    public Message() {
    }
    public Message(String messageType){
        this.messageType = messageType;
    }

    public Message(String senderName, String messageType){
        this.senderName = senderName;
        this.messageType = messageType;
    }
    public Message(String senderName, String receiverName, String messageContent, String sendTime, String messageType) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.messageContent = messageContent;
        this.sendTime = sendTime;
        this.messageType = messageType;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
