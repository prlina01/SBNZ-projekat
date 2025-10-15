package com.ftn.sbnz.service.config;

import com.ftn.sbnz.model.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;

@Configuration
public class DroolsConfig {

    private static final Logger log = LoggerFactory.getLogger(DroolsConfig.class);
    private static final double LOW_ALERT_THRESHOLD = 1.5d;   // 30% of 5
    private static final double HIGH_ALERT_THRESHOLD = 3.5d;  // 70% of 5

    private final EliminationRuleLoader eliminationRuleLoader;

    public DroolsConfig(EliminationRuleLoader eliminationRuleLoader) {
        this.eliminationRuleLoader = eliminationRuleLoader;
    }

    @Bean
    public KieSession kieSession(KieContainer kieContainer) {
        log.info("Initializing KieSession with complex data set...");
        eliminationRuleLoader.ensureEliminationRules(kieContainer.getKieBase("cepKbase"));
        KieSession kieSession = kieContainer.newKieSession("cepKsession");

    User user1_silver = new User(1L, "user1_silver");
    user1_silver.setStatus(User.UserStatus.SILVER);
    User user2_gold = new User(2L, "user2_gold");
    user2_gold.setStatus(User.UserStatus.GOLD);
    User user3_bronze = new User(3L, "user3_bronze");
    user3_bronze.setStatus(User.UserStatus.BRONZE);
    User user4_none = new User(4L, "user4_none");
    user4_none.setStatus(User.UserStatus.NONE);
    User user5_enterprise = new User(5L, "user5_enterprise");
    user5_enterprise.setStatus(User.UserStatus.GOLD);
    User user6_startup = new User(6L, "user6_startup");
    user6_startup.setStatus(User.UserStatus.NONE);

    List<User> users = Arrays.asList(
        user1_silver,
        user2_gold,
        user3_bronze,
        user4_none,
        user5_enterprise,
        user6_startup
    );

    // --- Providers ---
    Provider hetzner = new Provider(1L, "Hetzner");
    Provider scaleway = new Provider(2L, "Scaleway");
    Provider digitalOcean = new Provider(3L, "DigitalOcean");
    Provider vultr = new Provider(4L, "Vultr");
    Provider aws = new Provider(5L, "AWS");
    Provider google = new Provider(6L, "Google Cloud");
    Provider azure = new Provider(7L, "Azure");
    Provider linode = new Provider(8L, "Linode");
    Provider ibm = new Provider(9L, "IBM Cloud");
    Provider cloudflare = new Provider(10L, "Cloudflare Stream");

    // --- Servers ---
    Server server1_star = buildServer(1L, "Hetzner AX41", hetzner,
        Server.Purpose.ML_TRAINING, Server.CpuPerformance.HIGH,
        16, Server.GpuModel.NVIDIA_T4, 16,
        128, 2000, Server.StorageType.NVME,
        true, 1000, true, true,
        Server.Region.EU, true, 1.5, 950);
    server1_star.setDedicatedCpu(true);
    server1_star.setAutoscalingCapable(true);
    server1_star.setHybridDeployment(true);

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
    server3_neutral.setAutoscalingCapable(true);

    Server server4_high_velocity = buildServer(4L, "Vultr VC2", vultr,
        Server.Purpose.DATA_ANALYTICS, Server.CpuPerformance.HIGH,
        12, Server.GpuModel.NONE, 0,
        48, 512, Server.StorageType.NVME,
        true, 800, true, true,
        Server.Region.US, false, 0.28, 180);
    server4_high_velocity.setDedicatedCpu(true);
    server4_high_velocity.setAutoscalingCapable(true);

    Server server5_enterprise = buildServer(5L, "AWS M6i Large", aws,
        Server.Purpose.DATABASE, Server.CpuPerformance.HIGH,
        32, Server.GpuModel.NONE, 0,
        128, 1000, Server.StorageType.NVME,
        true, 2000, true, true,
        Server.Region.US, true, 2.6, 1400);
    server5_enterprise.setManagedService(true);
    server5_enterprise.setReplicationSupport(true);
    server5_enterprise.setMultiZone(true);

    Server server6_streaming = buildServer(6L, "Google C2 Stream", google,
        Server.Purpose.STREAMING, Server.CpuPerformance.MEDIUM,
        8, Server.GpuModel.NVIDIA_T4, 16,
        32, 500, Server.StorageType.NVME,
        true, 1200, true, true,
        Server.Region.APAC, true, 1.2, 780);
    server6_streaming.setEnergyEfficient(true);
    server6_streaming.setAutoscalingCapable(true);

    Server server7_ml_growth = buildServer(7L, "Azure NDv4", azure,
        Server.Purpose.ML_TRAINING, Server.CpuPerformance.HIGH,
        32, Server.GpuModel.NVIDIA_A100, 40,
        256, 4096, Server.StorageType.NVME,
        true, 1600, true, true,
        Server.Region.EU, false, 4.8, 3400);
    server7_ml_growth.setDedicatedCpu(true);
    server7_ml_growth.setAutoscalingCapable(true);
    server7_ml_growth.setHybridDeployment(true);
    server7_ml_growth.setOnPremiseAvailable(true);
    server7_ml_growth.setManagedService(true);
    server7_ml_growth.setReplicationSupport(true);
    server7_ml_growth.setMultiZone(true);
    server7_ml_growth.setStorageIops(2200);

    Server server8_web_scalable = buildServer(8L, "Linode App Scale", linode,
        Server.Purpose.WEB_APP, Server.CpuPerformance.MEDIUM,
        6, Server.GpuModel.NONE, 0,
        12, 240, Server.StorageType.SATA,
        true, 350, true, false,
        Server.Region.US, false, 0.09, 65);
    server8_web_scalable.setAutoscalingCapable(true);

    Server server9_db_guardian = buildServer(9L, "IBM Db2 Secure", ibm,
        Server.Purpose.DATABASE, Server.CpuPerformance.HIGH,
        24, Server.GpuModel.NONE, 0,
        128, 2000, Server.StorageType.NVME,
        true, 2200, true, true,
        Server.Region.EU, true, 3.8, 2100);
    server9_db_guardian.setManagedService(true);
    server9_db_guardian.setReplicationSupport(true);
    server9_db_guardian.setMultiZone(true);
    server9_db_guardian.setOnPremiseAvailable(true);
    server9_db_guardian.setHybridDeployment(true);
    server9_db_guardian.setStorageIops(1500);

    Server server10_edge_stream = buildServer(10L, "Cloudflare Stream Edge", cloudflare,
        Server.Purpose.STREAMING, Server.CpuPerformance.MEDIUM,
        12, Server.GpuModel.NVIDIA_T4, 16,
        48, 512, Server.StorageType.NVME,
        true, 1500, true, true,
        Server.Region.US, true, 0.95, 620);
    server10_edge_stream.setEnergyEfficient(true);
    server10_edge_stream.setAutoscalingCapable(true);
    server10_edge_stream.setDedicatedCpu(true);

    List<Server> servers = Arrays.asList(
        server1_star,
        server2_problem,
        server3_neutral,
        server4_high_velocity,
        server5_enterprise,
        server6_streaming,
        server7_ml_growth,
        server8_web_scalable,
        server9_db_guardian,
        server10_edge_stream
    );

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
    Rental rental11 = new Rental(11L, user5_enterprise, server7_ml_growth, daysAgo(210), null, "Enterprise model scaling");
    Rental rental12 = new Rental(12L, user2_gold, server7_ml_growth, daysAgo(195), null, "Automated training pipeline");
    Rental rental13 = new Rental(13L, user1_silver, server7_ml_growth, daysAgo(160), null, "Dataset labeling GPU farm");
    Rental rental14 = new Rental(14L, user3_bronze, server7_ml_growth, daysAgo(45), null, "Short-term inference");
    Rental rental15 = new Rental(15L, user4_none, server8_web_scalable, daysAgo(6), daysAgo(1), "Marketing microsite");
    Rental rental16 = new Rental(16L, user5_enterprise, server9_db_guardian, daysAgo(42), null, "Primary ledger");
    Rental rental17 = new Rental(17L, user2_gold, server9_db_guardian, daysAgo(5), null, "Hot standby");
    Rental rental18 = new Rental(18L, user1_silver, server10_edge_stream, daysAgo(14), null, "Live sports feed");
    Rental rental19 = new Rental(19L, user4_none, server10_edge_stream, daysAgo(9), daysAgo(2), "Concert streaming");
    Rental rental20 = new Rental(20L, user6_startup, server10_edge_stream, daysAgo(1), null, "Indie launch");
    Rental rental21 = new Rental(21L, user6_startup, server2_problem, daysAgo(4), null, "Promo landing");

    List<Rental> rentals = Arrays.asList(
        rental1,
        rental2,
        rental3,
        rental4,
        rental5,
        rental6,
        rental7,
        rental8,
        rental9,
        rental10,
        rental11,
        rental12,
        rental13,
        rental14,
        rental15,
        rental16,
        rental17,
        rental18,
        rental19,
        rental20,
        rental21
    );

    // --- Ratings ---
    Rating rating1 = new Rating(1L, user2_gold, server1_star, 4, daysAgo(8), rental1);
    Rating rating2 = new Rating(2L, user1_silver, server1_star, 4, daysAgo(3), rental2);
    Rating rating3 = new Rating(3L, user4_none, server2_problem, 3, daysAgo(1), rental3);
    Rating rating4 = new Rating(4L, user3_bronze, server4_high_velocity, 5, daysAgo(3), rental8);
    Rating rating5 = new Rating(5L, user1_silver, server5_enterprise, 2, daysAgo(7), rental9);
    Rating rating6 = new Rating(6L, user4_none, server6_streaming, 2, daysAgo(2), rental10);
    Rating rating7 = new Rating(7L, user5_enterprise, server7_ml_growth, 5, daysAgo(4), rental11);
    Rating rating8 = new Rating(8L, user2_gold, server7_ml_growth, 5, daysAgo(2), rental12);
    Rating rating9 = new Rating(9L, user1_silver, server7_ml_growth, 5, daysAgo(1), rental13);
    Rating rating10 = new Rating(10L, user3_bronze, server7_ml_growth, 4, daysAgo(1), rental14);
    Rating rating11 = new Rating(11L, user4_none, server8_web_scalable, 2, daysAgo(0), rental15);
    Rating rating12 = new Rating(12L, user3_bronze, server9_db_guardian, 2, daysAgo(6), rental16);
    Rating rating13 = new Rating(13L, user4_none, server9_db_guardian, 2, daysAgo(3), rental17);
    Rating rating14 = new Rating(14L, user4_none, server10_edge_stream, 2, daysAgo(4), rental18);
    Rating rating15 = new Rating(15L, user4_none, server10_edge_stream, 2, daysAgo(1), rental19);
    Rating rating16 = new Rating(16L, user6_startup, server10_edge_stream, 3, daysAgo(0), rental20);
    Rating rating17 = new Rating(17L, user6_startup, server2_problem, 2, daysAgo(0), rental21);

    List<Rating> ratings = Arrays.asList(
        rating1,
        rating2,
        rating3,
        rating4,
        rating5,
        rating6,
        rating7,
        rating8,
        rating9,
        rating10,
        rating11,
        rating12,
        rating13,
        rating14,
        rating15,
        rating16,
        rating17
    );

    List<Object> facts = new ArrayList<>();
    facts.addAll(users);
    facts.addAll(servers);
    facts.addAll(rentals);
    facts.addAll(ratings);
    facts.forEach(kieSession::insert);

    log.info("KieSession initialized with {} users, {} servers, {} rentals and {} ratings for CEP scenarios.",
        users.size(), servers.size(), rentals.size(), ratings.size());

        log.info("--- FIRING RULES ON INITIAL DATA SET ---");
        kieSession.fireAllRules();
        log.info("--- INITIAL RULE FIRING COMPLETE ---");

        log.info("--- PERFORMANCE SNAPSHOT ---");
        Map<String, PerformanceEvent> snapshotByServer = new LinkedHashMap<>();
        for (FactHandle handle : kieSession.getFactHandles(obj -> obj instanceof PerformanceEvent)) {
            PerformanceEvent event = (PerformanceEvent) kieSession.getObject(handle);
            if (event != null && event.getServer() != null) {
                String key = event.getServer().getId() != null
                    ? event.getServer().getId().toString()
                    : event.getServer().getName();
                snapshotByServer.put(key, event);
            }
        }

        snapshotByServer.values().forEach(event -> {
            double percentage = (event.getAverageRating() / 5.0d) * 100.0d;
            String formatted = String.format(Locale.ROOT, "%.2f%% (avg %.2f)", percentage, event.getAverageRating());
            String serverName = event.getServer().getName();

            if (event.getAverageRating() < LOW_ALERT_THRESHOLD) {
                log.warn("SNAPSHOT LOW (<30%): {} -> {}", serverName, formatted);
            } else if (event.getAverageRating() > HIGH_ALERT_THRESHOLD) {
                log.info("SNAPSHOT POSITIVE (>70%): {} -> {}", serverName, formatted);
            } else {
                log.info("SNAPSHOT STABLE: {} -> {}", serverName, formatted);
            }
        });

        // Log initial reports
        Set<String> loggedReportMessages = new LinkedHashSet<>();
        for (FactHandle handle : kieSession.getFactHandles(obj -> obj instanceof PerformanceReport)) {
            PerformanceReport report = (PerformanceReport) kieSession.getObject(handle);
            if (report != null && report.getMessage() != null && loggedReportMessages.add(report.getMessage())) {
                if (report.getMessage().contains("critically low")) {
                    log.warn("INITIAL ADMIN REPORT: {}", report.getMessage());
                } else {
                    log.info("INITIAL ADMIN REPORT: {}", report.getMessage());
                }
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