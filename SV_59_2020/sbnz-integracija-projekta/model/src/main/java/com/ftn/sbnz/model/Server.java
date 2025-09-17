package com.ftn.sbnz.model;

public class Server {

    public enum Purpose {
        WEB_APP, DATABASE, DATA_ANALYTICS, ML_TRAINING, ML_INFERENCE, STREAMING
    }

    public enum CpuPerformance {
        LOW, MEDIUM, HIGH
    }

    public enum GpuModel {
        NONE, NVIDIA_A100, NVIDIA_V100, NVIDIA_T4
    }

    public enum StorageType {
        NVME, SATA
    }

    public enum Region {
        EU, US, APAC
    }

    private Long id;
    private String name;
    private Provider provider;
    private Purpose purpose;
    private CpuPerformance cpuPerformance;
    private int vcpuCount;
    private GpuModel gpuModel;
    private int gpuVram;
    private int ram; // in GB
    private int storageCapacity; // in GB
    private StorageType storageType;
    private boolean encryptedStorage;
    private int networkBandwidth; // in Mbps
    private boolean ddosProtection;
    private boolean highAvailability;
    private Region region;
    private boolean ecoFriendly;
    private double pricePerHour;
    private double pricePerMonth;
    private int score;

    public Server() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Purpose getPurpose() {
        return purpose;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    public CpuPerformance getCpuPerformance() {
        return cpuPerformance;
    }

    public void setCpuPerformance(CpuPerformance cpuPerformance) {
        this.cpuPerformance = cpuPerformance;
    }

    public int getVcpuCount() {
        return vcpuCount;
    }

    public void setVcpuCount(int vcpuCount) {
        this.vcpuCount = vcpuCount;
    }

    public GpuModel getGpuModel() {
        return gpuModel;
    }

    public void setGpuModel(GpuModel gpuModel) {
        this.gpuModel = gpuModel;
    }

    public int getGpuVram() {
        return gpuVram;
    }

    public void setGpuVram(int gpuVram) {
        this.gpuVram = gpuVram;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getStorageCapacity() {
        return storageCapacity;
    }

    public void setStorageCapacity(int storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public boolean isEncryptedStorage() {
        return encryptedStorage;
    }

    public void setEncryptedStorage(boolean encryptedStorage) {
        this.encryptedStorage = encryptedStorage;
    }

    public int getNetworkBandwidth() {
        return networkBandwidth;
    }

    public void setNetworkBandwidth(int networkBandwidth) {
        this.networkBandwidth = networkBandwidth;
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

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public boolean isEcoFriendly() {
        return ecoFriendly;
    }

    public void setEcoFriendly(boolean ecoFriendly) {
        this.ecoFriendly = ecoFriendly;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public double getPricePerMonth() {
        return pricePerMonth;
    }

    public void setPricePerMonth(double pricePerMonth) {
        this.pricePerMonth = pricePerMonth;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
