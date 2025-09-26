package com.ftn.sbnz.service.dto;

public class RentalDTO {
    private Long userId;
    private Long serverId;
    private String purpose;

    public RentalDTO() {
    }

    public RentalDTO(Long userId, Long serverId, String purpose) {
        this.userId = userId;
        this.serverId = serverId;
        this.purpose = purpose;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
