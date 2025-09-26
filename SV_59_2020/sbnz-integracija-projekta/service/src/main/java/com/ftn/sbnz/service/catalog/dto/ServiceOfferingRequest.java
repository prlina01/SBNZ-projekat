package com.ftn.sbnz.service.catalog.dto;

import com.ftn.sbnz.model.Server;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ServiceOfferingRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String provider;

    @NotNull
    private Server.Purpose purpose;

    @NotNull
    private Server.CpuPerformance cpuPerformance;

    @Min(1)
    private int vcpuCount;

    @NotNull
    private Server.GpuModel gpuModel;

    private int gpuVram;

    @Min(1)
    private int ram;

    @Min(1)
    private int storageCapacity;

    @NotNull
    private Server.StorageType storageType;

    private boolean encryptedStorage;
    private int networkBandwidth;
    private boolean ddosProtection;
    private boolean highAvailability;

    @NotNull
    private Server.Region region;

    private boolean ecoFriendly;
    private double pricePerHour;
    private double pricePerMonth;

    // Getters and setters omitted for brevity

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
}
