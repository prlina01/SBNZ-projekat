package com.ftn.sbnz.service.catalog.dto;

import com.ftn.sbnz.model.SearchFilters;
import com.ftn.sbnz.model.Server;

import javax.validation.constraints.Min;

public class RecommendationRequest {

    private Server.Purpose purpose;
    private Server.CpuPerformance cpuPerformance;
    private Integer minVcpuCount;
    private Boolean gpuRequired;
    private Integer minGpuVram;
    private Integer minRam;
    private Integer minStorageCapacity;
    private Server.StorageType storageType;
    private Boolean encryptedStorage;
    private Integer minNetworkBandwidth;
    private Boolean ddosProtection;
    private Boolean highAvailability;
    private Server.Region region;
    private Boolean ecoPriority;
    private Integer concurrentUsers;
    private SearchFilters.Budget budget;
    private Integer rentalDuration;

    @Min(1)
    private Integer limit;

    public Server.Purpose getPurpose() {
        return purpose;
    }

    public void setPurpose(Server.Purpose purpose) {
        this.purpose = purpose;
    }

    public Server.CpuPerformance getCpuPerformance() {
        return cpuPerformance;
    }

    public void setCpuPerformance(Server.CpuPerformance cpuPerformance) {
        this.cpuPerformance = cpuPerformance;
    }

    public Integer getMinVcpuCount() {
        return minVcpuCount;
    }

    public void setMinVcpuCount(Integer minVcpuCount) {
        this.minVcpuCount = minVcpuCount;
    }

    public Boolean getGpuRequired() {
        return gpuRequired;
    }

    public void setGpuRequired(Boolean gpuRequired) {
        this.gpuRequired = gpuRequired;
    }

    public Integer getMinGpuVram() {
        return minGpuVram;
    }

    public void setMinGpuVram(Integer minGpuVram) {
        this.minGpuVram = minGpuVram;
    }

    public Integer getMinRam() {
        return minRam;
    }

    public void setMinRam(Integer minRam) {
        this.minRam = minRam;
    }

    public Integer getMinStorageCapacity() {
        return minStorageCapacity;
    }

    public void setMinStorageCapacity(Integer minStorageCapacity) {
        this.minStorageCapacity = minStorageCapacity;
    }

    public Server.StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(Server.StorageType storageType) {
        this.storageType = storageType;
    }

    public Boolean getEncryptedStorage() {
        return encryptedStorage;
    }

    public void setEncryptedStorage(Boolean encryptedStorage) {
        this.encryptedStorage = encryptedStorage;
    }

    public Integer getMinNetworkBandwidth() {
        return minNetworkBandwidth;
    }

    public void setMinNetworkBandwidth(Integer minNetworkBandwidth) {
        this.minNetworkBandwidth = minNetworkBandwidth;
    }

    public Boolean getDdosProtection() {
        return ddosProtection;
    }

    public void setDdosProtection(Boolean ddosProtection) {
        this.ddosProtection = ddosProtection;
    }

    public Boolean getHighAvailability() {
        return highAvailability;
    }

    public void setHighAvailability(Boolean highAvailability) {
        this.highAvailability = highAvailability;
    }

    public Server.Region getRegion() {
        return region;
    }

    public void setRegion(Server.Region region) {
        this.region = region;
    }

    public Boolean getEcoPriority() {
        return ecoPriority;
    }

    public void setEcoPriority(Boolean ecoPriority) {
        this.ecoPriority = ecoPriority;
    }

    public Integer getConcurrentUsers() {
        return concurrentUsers;
    }

    public void setConcurrentUsers(Integer concurrentUsers) {
        this.concurrentUsers = concurrentUsers;
    }

    public SearchFilters.Budget getBudget() {
        return budget;
    }

    public void setBudget(SearchFilters.Budget budget) {
        this.budget = budget;
    }

    public Integer getRentalDuration() {
        return rentalDuration;
    }

    public void setRentalDuration(Integer rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
