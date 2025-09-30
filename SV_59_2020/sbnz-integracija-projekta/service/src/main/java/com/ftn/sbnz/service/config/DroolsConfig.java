package com.ftn.sbnz.service.config;

import com.ftn.sbnz.model.*;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Configuration
public class DroolsConfig {

    private static final Logger log = LoggerFactory.getLogger(DroolsConfig.class);

    @Bean
    public KieSession kieSession(KieContainer kieContainer) {
        log.info("Initializing KieSession with complex data set...");
        KieSession kieSession = kieContainer.newKieSession("cepKsession");

    User user1_silver = new User(1L, "user1_silver");
        user1_silver.setStatus(User.UserStatus.SILVER);
        User user2_gold = new User(2L, "user2_gold");
        user2_gold.setStatus(User.UserStatus.GOLD);
        User user3_bronze = new User(3L, "user3_bronze");
        user3_bronze.setStatus(User.UserStatus.BRONZE);
        User user4_none = new User(4L, "user4_none");
        user4_none.setStatus(User.UserStatus.NONE);

    // --- Providers ---
    Provider hetzner = new Provider(1L, "Hetzner");
    Provider scaleway = new Provider(2L, "Scaleway");
    Provider digitalOcean = new Provider(3L, "DigitalOcean");
    Provider vultr = new Provider(4L, "Vultr");
    Provider aws = new Provider(5L, "AWS");
    Provider google = new Provider(6L, "Google Cloud");

    // --- Servers ---
    Server server1_star = buildServer(1L, "Hetzner AX41", hetzner,
        Server.Purpose.ML_TRAINING, Server.CpuPerformance.HIGH,
        16, Server.GpuModel.NVIDIA_T4, 16,
        128, 2000, Server.StorageType.NVME,
        true, 1000, true, true,
        Server.Region.EU, true, 1.5, 950);

    Server server2_problem = buildServer(2L, "Scaleway Stardust", scaleway,
        Server.Purpose.WEB_APP, Server.CpuPerformance.LOW,
        2, Server.GpuModel.NONE, 0,
        4, 50, Server.StorageType.SATA,
        false, 100, false, false,
        Server.Region.EU, false, 0.006, 4.5);

    Server server3_neutral = buildServer(3L, "DigitalOcean Droplet", digitalOcean,
        Server.Purpose.WEB_APP, Server.CpuPerformance.MEDIUM,
        8, Server.GpuModel.NONE, 0,
        16, 320, Server.StorageType.NVME,
        true, 400, true, true,
        Server.Region.US, true, 0.14, 95);

    Server server4_high_velocity = buildServer(4L, "Vultr VC2", vultr,
        Server.Purpose.DATA_ANALYTICS, Server.CpuPerformance.HIGH,
        12, Server.GpuModel.NONE, 0,
        48, 512, Server.StorageType.NVME,
        true, 800, true, true,
        Server.Region.US, false, 0.28, 180);

    Server server5_enterprise = buildServer(5L, "AWS M6i Large", aws,
        Server.Purpose.DATABASE, Server.CpuPerformance.HIGH,
        32, Server.GpuModel.NONE, 0,
        128, 1000, Server.StorageType.NVME,
        true, 2000, true, true,
        Server.Region.US, true, 2.6, 1400);

    Server server6_streaming = buildServer(6L, "Google C2 Stream", google,
        Server.Purpose.STREAMING, Server.CpuPerformance.MEDIUM,
        8, Server.GpuModel.NVIDIA_T4, 16,
        32, 500, Server.StorageType.NVME,
        true, 1200, true, true,
        Server.Region.APAC, true, 1.2, 780);

    // --- Rentals ---
    Rental rental1 = new Rental(1L, user2_gold, server1_star, daysAgo(120), daysAgo(10), "ML");
    Rental rental2 = new Rental(2L, user1_silver, server1_star, daysAgo(60), daysAgo(5), "Database backup");
    Rental rental3 = new Rental(3L, user4_none, server2_problem, daysAgo(10), daysAgo(2), "Landing page");
    Rental rental4 = new Rental(4L, user3_bronze, server3_neutral, daysAgo(25), daysAgo(5), "E-commerce");
    Rental rental5 = new Rental(5L, user1_silver, server4_high_velocity, daysAgo(20), daysAgo(15), "Analytics");
    Rental rental6 = new Rental(6L, user2_gold, server4_high_velocity, daysAgo(15), daysAgo(10), "ML feature store");
    Rental rental7 = new Rental(7L, user3_bronze, server4_high_velocity, daysAgo(10), daysAgo(5), "Visualization");
    Rental rental8 = new Rental(8L, user4_none, server4_high_velocity, daysAgo(120), daysAgo(1), "Database");
    Rental rental9 = new Rental(9L, user2_gold, server5_enterprise, daysAgo(45), null, "Production database");
    Rental rental10 = new Rental(10L, user1_silver, server6_streaming, daysAgo(12), null, "Live streaming");

    // --- Ratings ---
    Rating rating1 = new Rating(1L, user2_gold, server1_star, 5, daysAgo(8), rental1);
    Rating rating2 = new Rating(2L, user1_silver, server1_star, 5, daysAgo(3), rental2);
    Rating rating3 = new Rating(3L, user4_none, server2_problem, 1, daysAgo(1), rental3);
    Rating rating4 = new Rating(4L, user3_bronze, server4_high_velocity, 5, daysAgo(3), rental8);
    Rating rating5 = new Rating(5L, user2_gold, server5_enterprise, 4, daysAgo(7), rental9);
    Rating rating6 = new Rating(6L, user1_silver, server6_streaming, 5, daysAgo(2), rental10);
       
        kieSession.insert(user1_silver);
        kieSession.insert(user2_gold);
        kieSession.insert(user3_bronze);
        kieSession.insert(user4_none);

    kieSession.insert(server1_star);
    kieSession.insert(server2_problem);
    kieSession.insert(server3_neutral);
    kieSession.insert(server4_high_velocity);
    kieSession.insert(server5_enterprise);
    kieSession.insert(server6_streaming);

        kieSession.insert(rental1);
        kieSession.insert(rental2);
        kieSession.insert(rental3);
        kieSession.insert(rental4);
        kieSession.insert(rental5);
        kieSession.insert(rental6);
        kieSession.insert(rental7);
    kieSession.insert(rental8);
    kieSession.insert(rental9);
    kieSession.insert(rental10);

        kieSession.insert(rating1);
        kieSession.insert(rating2);
        kieSession.insert(rating3);
    kieSession.insert(rating4);
    kieSession.insert(rating5);
    kieSession.insert(rating6);
        log.info("KieSession initialized and complex data set inserted.");

        log.info("--- FIRING RULES ON INITIAL DATA SET ---");
        kieSession.fireAllRules();
        log.info("--- INITIAL RULE FIRING COMPLETE ---");


                // Log initial reports
        for (FactHandle handle : kieSession.getFactHandles(obj -> obj instanceof PerformanceReport)) {
            PerformanceReport report = (PerformanceReport) kieSession.getObject(handle);
            if (report.getMessage().contains("critically low")) {
                 log.warn("INITIAL ADMIN REPORT: {}", report.getMessage());
            } else {
                 log.info("INITIAL ADMIN REPORT: {}", report.getMessage());
            }
            kieSession.delete(handle);
        }


        return kieSession;
    }

    private Server buildServer(Long id, String name, Provider provider,
                               Server.Purpose purpose, Server.CpuPerformance cpuPerformance,
                               int vcpuCount, Server.GpuModel gpuModel, int gpuVram,
                               int ram, int storageCapacity, Server.StorageType storageType,
                               boolean encryptedStorage, int bandwidth, boolean ddosProtection,
                               boolean highAvailability, Server.Region region,
                               boolean ecoFriendly, double pricePerHour, double pricePerMonth) {
        Server server = new Server();
        server.setId(id);
        server.setName(name);
        server.setProvider(provider);
        server.setPurpose(purpose);
        server.setCpuPerformance(cpuPerformance);
        server.setVcpuCount(vcpuCount);
        server.setGpuModel(gpuModel);
        server.setGpuVram(gpuVram);
        server.setRam(ram);
        server.setStorageCapacity(storageCapacity);
        server.setStorageType(storageType);
        server.setEncryptedStorage(encryptedStorage);
        server.setNetworkBandwidth(bandwidth);
        server.setDdosProtection(ddosProtection);
        server.setHighAvailability(highAvailability);
        server.setRegion(region);
        server.setEcoFriendly(ecoFriendly);
        server.setPricePerHour(pricePerHour);
        server.setPricePerMonth(pricePerMonth);
        server.setScore(0);
        return server;
    }

    private Date daysAgo(int days) {
        return new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days));
    }
}