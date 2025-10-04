package com.ftn.sbnz.service.catalog.dto;

import com.ftn.sbnz.model.Server;

import java.util.ArrayList;
import java.util.List;

public class ServiceOfferingResponse {

    private Long id;
    private String name;
    private String provider;
    private Server.Purpose purpose;
    private Server.CpuPerformance cpuPerformance;
    private int vcpuCount;
    private Server.GpuModel gpuModel;
    private int gpuVram;
    private int ram;
    private int storageCapacity;
    private Server.StorageType storageType;
    private boolean encryptedStorage;
    private int networkBandwidth;
    private boolean ddosProtection;
    private boolean highAvailability;
    private Server.Region region;
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
    private double matchScore;
    private List<String> highlights = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private long activeRentalCount;
    private Double averageRating;

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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

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

    public int getVcpuCount() {
        return vcpuCount;
    }

    public void setVcpuCount(int vcpuCount) {
        this.vcpuCount = vcpuCount;
    }

    public Server.GpuModel getGpuModel() {
        return gpuModel;
    }

    public void setGpuModel(Server.GpuModel gpuModel) {
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

    public Server.Region getRegion() {
        return region;
    }

    public void setRegion(Server.Region region) {
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

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<String> highlights) {
        this.highlights = highlights != null ? new ArrayList<>(highlights) : new ArrayList<>();
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings != null ? new ArrayList<>(warnings) : new ArrayList<>();
    }

    public long getActiveRentalCount() {
        return activeRentalCount;
    }

    public void setActiveRentalCount(long activeRentalCount) {
        this.activeRentalCount = Math.max(activeRentalCount, 0);
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        if (averageRating == null) {
            this.averageRating = null;
        } else {
            this.averageRating = Math.max(0d, averageRating);
        }
    }
}
