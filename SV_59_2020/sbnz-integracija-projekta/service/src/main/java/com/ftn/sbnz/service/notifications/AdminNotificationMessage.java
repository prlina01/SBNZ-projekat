package com.ftn.sbnz.service.notifications;

public class AdminNotificationMessage {

    public enum Type {
        POSITIVE,
        NEGATIVE
    }

    private Type type;
    private Long serverId;
    private String serverName;
    private String providerName;
    private String content;
    private String originalMessage;
    private long timestamp;

    public AdminNotificationMessage() {
    }

    public AdminNotificationMessage(Type type, Long serverId, String serverName, String providerName, String content, String originalMessage, long timestamp) {
        this.type = type;
        this.serverId = serverId;
        this.serverName = serverName;
        this.providerName = providerName;
        this.content = content;
        this.originalMessage = originalMessage;
        this.timestamp = timestamp;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
