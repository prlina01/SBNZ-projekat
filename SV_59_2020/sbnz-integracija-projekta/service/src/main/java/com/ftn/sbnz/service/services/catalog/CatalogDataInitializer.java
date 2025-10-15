package com.ftn.sbnz.service.services.catalog;

import com.ftn.sbnz.model.Server;
import com.ftn.sbnz.model.catalog.ServiceOffering;
import com.ftn.sbnz.service.repositories.ServiceOfferingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CatalogDataInitializer implements CommandLineRunner {

    private final ServiceOfferingRepository repository;

    public CatalogDataInitializer(ServiceOfferingRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        repository.saveAll(List.of(
                buildOffering("Hetzner AX41", "Hetzner",
                        Server.Purpose.ML_TRAINING, Server.CpuPerformance.HIGH,
                        16, Server.GpuModel.NVIDIA_T4, 16,
                        128, 2000, Server.StorageType.NVME,
                        true, 1000, true, true,
                        Server.Region.EU, true, 1.5, 950),
                buildOffering("Scaleway Stardust", "Scaleway",
                        Server.Purpose.WEB_APP, Server.CpuPerformance.LOW,
                        2, Server.GpuModel.NONE, 0,
                        4, 50, Server.StorageType.SATA,
                        false, 100, false, false,
                        Server.Region.EU, false, 0.006, 4.5),
                buildOffering("DigitalOcean Droplet", "DigitalOcean",
                        Server.Purpose.WEB_APP, Server.CpuPerformance.MEDIUM,
                        8, Server.GpuModel.NONE, 0,
                        16, 320, Server.StorageType.NVME,
                        true, 400, true, true,
                        Server.Region.US, true, 0.14, 95),
                buildOffering("Vultr VC2", "Vultr",
                        Server.Purpose.DATA_ANALYTICS, Server.CpuPerformance.HIGH,
                        12, Server.GpuModel.NONE, 0,
                        48, 512, Server.StorageType.NVME,
                        true, 800, true, true,
                        Server.Region.US, false, 0.28, 180),
                buildOffering("AWS M6i Large", "AWS",
                        Server.Purpose.DATABASE, Server.CpuPerformance.HIGH,
                        32, Server.GpuModel.NONE, 0,
                        128, 1000, Server.StorageType.NVME,
                        true, 2000, true, true,
                        Server.Region.US, true, 2.6, 1400),
                buildOffering("Google C2 Stream", "Google Cloud",
                        Server.Purpose.STREAMING, Server.CpuPerformance.MEDIUM,
                        8, Server.GpuModel.NVIDIA_T4, 16,
                        32, 500, Server.StorageType.NVME,
                        true, 1200, true, true,
                        Server.Region.APAC, true, 1.2, 780),
                buildOffering("Azure NDv4", "Azure",
                        Server.Purpose.ML_TRAINING, Server.CpuPerformance.HIGH,
                        32, Server.GpuModel.NVIDIA_A100, 40,
                        256, 4096, Server.StorageType.NVME,
                        true, 1600, true, true,
                        Server.Region.EU, false, 4.8, 3400),
                buildOffering("Linode App Scale", "Linode",
                        Server.Purpose.WEB_APP, Server.CpuPerformance.MEDIUM,
                        6, Server.GpuModel.NONE, 0,
                        12, 240, Server.StorageType.SATA,
                        true, 350, true, false,
                        Server.Region.US, false, 0.09, 65),
                buildOffering("IBM Db2 Secure", "IBM",
                        Server.Purpose.DATABASE, Server.CpuPerformance.HIGH,
                        24, Server.GpuModel.NONE, 0,
                        128, 2000, Server.StorageType.NVME,
                        true, 2200, true, true,
                        Server.Region.EU, true, 3.8, 2100),
                buildOffering("Cloudflare Stream Edge", "Cloudflare",
                        Server.Purpose.STREAMING, Server.CpuPerformance.MEDIUM,
                        12, Server.GpuModel.NVIDIA_T4, 16,
                        48, 512, Server.StorageType.NVME,
                        true, 1500, true, true,
                        Server.Region.US, true, 0.95, 620)
        ));
    }

    private ServiceOffering buildOffering(String name,
                                          String provider,
                                          Server.Purpose purpose,
                                          Server.CpuPerformance cpuPerformance,
                                          int vcpuCount,
                                          Server.GpuModel gpuModel,
                                          int gpuVram,
                                          int ram,
                                          int storageCapacity,
                                          Server.StorageType storageType,
                                          boolean encryptedStorage,
                                          int networkBandwidth,
                                          boolean ddosProtection,
                                          boolean highAvailability,
                                          Server.Region region,
                                          boolean ecoFriendly,
                                          double pricePerHour,
                                          double pricePerMonth) {
        ServiceOffering offering = new ServiceOffering();
        offering.setName(name);
        offering.setProviderName(provider);
        offering.setPurpose(purpose);
        offering.setCpuPerformance(cpuPerformance);
        offering.setVcpuCount(vcpuCount);
        offering.setGpuModel(gpuModel);
        offering.setGpuVram(gpuVram);
        offering.setRam(ram);
        offering.setStorageCapacity(storageCapacity);
        offering.setStorageType(storageType);
        offering.setEncryptedStorage(encryptedStorage);
        offering.setNetworkBandwidth(networkBandwidth);
        offering.setDdosProtection(ddosProtection);
        offering.setHighAvailability(highAvailability);
        offering.setRegion(region);
        offering.setEcoFriendly(ecoFriendly);
        offering.setPricePerHour(pricePerHour);
        offering.setPricePerMonth(pricePerMonth);
        return offering;
    }
}
