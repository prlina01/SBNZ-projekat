package com.ftn.sbnz.model.catalog;

import com.ftn.sbnz.model.Provider;
import com.ftn.sbnz.model.Server;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "service_offerings")
public class ServiceOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String providerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Server.Purpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Server.CpuPerformance cpuPerformance;

    @Column(nullable = false)
    private int vcpuCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Server.GpuModel gpuModel;

    private int gpuVram;

    @Column(nullable = false)
    private int ram;

    @Column(nullable = false)
    private int storageCapacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Server.StorageType storageType;

    private boolean encryptedStorage;
    private int networkBandwidth;
    private boolean ddosProtection;
    private boolean highAvailability;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
        server.setDedicatedCpu(dedicatedCpu);
        server.setAutoscalingCapable(autoscalingCapable);
        server.setManagedService(managedService);
        server.setReplicationSupport(replicationSupport);
        server.setMultiZone(multiZone);
        server.setOnPremiseAvailable(onPremiseAvailable);
        server.setHybridDeployment(hybridDeployment);
        server.setEnergyEfficient(energyEfficient);
        server.setStorageIops(storageIops);
        server.setPricePerHour(pricePerHour);
        server.setPricePerMonth(pricePerMonth);
        server.setBasePricePerHour(pricePerHour);
        server.setBasePricePerMonth(pricePerMonth);
        server.setAppliedDiscountRate(null);
        server.setRuleHighlights(null);
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
}
