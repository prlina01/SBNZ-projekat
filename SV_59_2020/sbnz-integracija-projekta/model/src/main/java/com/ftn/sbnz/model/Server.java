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
    private boolean dedicatedCpu;
    private boolean autoscalingCapable;
    private boolean managedService;
    private boolean replicationSupport;
    private boolean multiZone;
    private boolean onPremiseAvailable;
    private boolean hybridDeployment;
    private boolean energyEfficient;
    private int storageIops;
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

    public boolean isDedicatedCpu() {
        return dedicatedCpu;
    }

    public void setDedicatedCpu(boolean dedicatedCpu) {
        this.dedicatedCpu = dedicatedCpu;
    }

    public boolean isAutoscalingCapable() {
        return autoscalingCapable;
    }

    public void setAutoscalingCapable(boolean autoscalingCapable) {
        this.autoscalingCapable = autoscalingCapable;
    }

    public boolean isManagedService() {
        return managedService;
    }

    public void setManagedService(boolean managedService) {
        this.managedService = managedService;
    }

    public boolean isReplicationSupport() {
        return replicationSupport;
    }

    public void setReplicationSupport(boolean replicationSupport) {
        this.replicationSupport = replicationSupport;
    }

    public boolean isMultiZone() {
        return multiZone;
    }

    public void setMultiZone(boolean multiZone) {
        this.multiZone = multiZone;
    }

    public boolean isOnPremiseAvailable() {
        return onPremiseAvailable;
    }

    public void setOnPremiseAvailable(boolean onPremiseAvailable) {
        this.onPremiseAvailable = onPremiseAvailable;
    }

    public boolean isHybridDeployment() {
        return hybridDeployment;
    }

    public void setHybridDeployment(boolean hybridDeployment) {
        this.hybridDeployment = hybridDeployment;
    }

    public boolean isEnergyEfficient() {
        return energyEfficient;
    }

    public void setEnergyEfficient(boolean energyEfficient) {
        this.energyEfficient = energyEfficient;
    }

    public int getStorageIops() {
        return storageIops;
    }

    public void setStorageIops(int storageIops) {
        this.storageIops = storageIops;
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
