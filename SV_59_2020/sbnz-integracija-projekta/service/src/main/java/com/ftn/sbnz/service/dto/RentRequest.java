package com.ftn.sbnz.service.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RentRequest {

    @NotNull
    private Long serviceOfferingId;

    @NotBlank
    private String purpose;

    @Min(1)
    @Max(1095)
    private int durationDays;

    public RentRequest() {
    }

    public RentRequest(Long serviceOfferingId, String purpose, int durationDays) {
        this.serviceOfferingId = serviceOfferingId;
        this.purpose = purpose;
        this.durationDays = durationDays;
    }

    public Long getServiceOfferingId() {
        return serviceOfferingId;
    }

    public void setServiceOfferingId(Long serviceOfferingId) {
        this.serviceOfferingId = serviceOfferingId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }
}
