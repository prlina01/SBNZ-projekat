package com.ftn.sbnz.model;

public class SearchFilters {

    private Server.Purpose purpose;
    private Server.CpuPerformance cpuPerformance;
    private int minVcpuCount;
    private boolean gpuRequired;
    private int minGpuVram;
    private int minRam;
    private int minStorageCapacity;
    private Server.StorageType storageType;
    private boolean encryptedStorage;
    private int minNetworkBandwidth;
    private boolean ddosProtection;
    private boolean highAvailability;
    private Server.Region region;
    private boolean ecoPriority;
    private int concurrentUsers;
    private Budget budget;
    private int rentalDuration; // in days
    private int datasetSizeGb;
    private boolean gdprRequired;

    public enum Budget {
        LOW, MEDIUM, HIGH
    }

    public SearchFilters() {
    }

    // Getters and Setters

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

    public int getMinVcpuCount() {
        return minVcpuCount;
    }

    public void setMinVcpuCount(int minVcpuCount) {
        this.minVcpuCount = minVcpuCount;
    }

    public boolean isGpuRequired() {
        return gpuRequired;
    }

    public void setGpuRequired(boolean gpuRequired) {
        this.gpuRequired = gpuRequired;
    }

    public int getMinGpuVram() {
        return minGpuVram;
    }

    public void setMinGpuVram(int minGpuVram) {
        this.minGpuVram = minGpuVram;
    }

    public int getMinRam() {
        return minRam;
    }

    public void setMinRam(int minRam) {
        this.minRam = minRam;
    }

    public int getMinStorageCapacity() {
        return minStorageCapacity;
    }

    public void setMinStorageCapacity(int minStorageCapacity) {
        this.minStorageCapacity = minStorageCapacity;
    }

    public Server.StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(Server.StorageType storageType) {
        this.storageType = storageType;
    }

    public boolean isEncryptedStorage() {
        return encryptedStorage;
    }

    public void setEncryptedStorage(boolean encryptedStorage) {
        this.encryptedStorage = encryptedStorage;
    }

    public int getMinNetworkBandwidth() {
        return minNetworkBandwidth;
    }

    public void setMinNetworkBandwidth(int minNetworkBandwidth) {
        this.minNetworkBandwidth = minNetworkBandwidth;
    }

    public boolean isDdosProtection() {
        return ddosProtection;
    }

    public void setDdosProtection(boolean ddosProtection) {
        this.ddosProtection = ddosProtection;
    }

    public boolean isHighAvailability() {
        return highAvailability;
    }

    public void setHighAvailability(boolean highAvailability) {
        this.highAvailability = highAvailability;
    }

    public Server.Region getRegion() {
        return region;
    }

    public void setRegion(Server.Region region) {
        this.region = region;
    }

    public boolean isEcoPriority() {
        return ecoPriority;
    }

    public void setEcoPriority(boolean ecoPriority) {
        this.ecoPriority = ecoPriority;
    }

    public int getConcurrentUsers() {
        return concurrentUsers;
    }

    public void setConcurrentUsers(int concurrentUsers) {
        this.concurrentUsers = concurrentUsers;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public int getRentalDuration() {
        return rentalDuration;
    }

    public void setRentalDuration(int rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    public int getDatasetSizeGb() {
        return datasetSizeGb;
    }

    public void setDatasetSizeGb(int datasetSizeGb) {
        this.datasetSizeGb = Math.max(0, datasetSizeGb);
    }

    public boolean isGdprRequired() {
        return gdprRequired;
    }

    public void setGdprRequired(boolean gdprRequired) {
        this.gdprRequired = gdprRequired;
    }
}
