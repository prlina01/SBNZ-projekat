package com.ftn.sbnz.model.catalog;

import com.ftn.sbnz.model.Provider;
import com.ftn.sbnz.model.Server;

public class ServiceOffering {
    private Long id;
    private String name;
    private String providerName;
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
    private double pricePerHour;
    private double pricePerMonth;

    public ServiceOffering() {
    }

    public ServiceOffering(Long id, String name, String providerName) {
        this.id = id;
        this.name = name;
        this.providerName = providerName;
    }

    public Server toServer() {
        Server server = new Server();
        server.setId(id);
        server.setName(name);
        server.setProvider(new Provider(null, providerName));
        server.setPurpose(purpose);
        server.setCpuPerformance(cpuPerformance);
        server.setVcpuCount(vcpuCount);
        server.setGpuModel(gpuModel);
        server.setGpuVram(gpuVram);
        server.setRam(ram);
        server.setStorageCapacity(storageCapacity);
        server.setStorageType(storageType);
        server.setEncryptedStorage(encryptedStorage);
        server.setNetworkBandwidth(networkBandwidth);
        server.setDdosProtection(ddosProtection);
        server.setHighAvailability(highAvailability);
        server.setRegion(region);
        server.setEcoFriendly(ecoFriendly);
        server.setPricePerHour(pricePerHour);
        server.setPricePerMonth(pricePerMonth);
        server.setScore(0);
        return server;
    }

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

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
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
