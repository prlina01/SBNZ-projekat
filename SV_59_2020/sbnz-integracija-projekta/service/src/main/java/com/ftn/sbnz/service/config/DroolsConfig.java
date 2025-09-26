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

        // --- Servers ---
        Server server1_star = new Server();
        server1_star.setId(1L);
        server1_star.setName("Hetzner AX41"); // STARTS HIGH

        Server server2_problem = new Server();
        server2_problem.setId(2L);
        server2_problem.setName("Scaleway Stardust"); // STARTS LOW

        Server server3_neutral = new Server();
        server3_neutral.setId(3L);
        server3_neutral.setName("DigitalOcean Droplet"); // NEUTRAL

        Server server4_high_velocity = new Server();
        server4_high_velocity.setId(4L);
        server4_high_velocity.setName("Vultr VC2"); // HIGH VELOCITY

        // --- Rentals ---
        // Server 1 (Star): Long duration rentals
        Rental rental1 = new Rental(1L, user2_gold, server1_star, daysAgo(120), daysAgo(10), "ML");
        Rental rental2 = new Rental(2L, user1_silver, server1_star, daysAgo(60), daysAgo(5), "DB");
        
        // Server 2 (Problem): Short duration, recent
        Rental rental3 = new Rental(3L, user4_none, server2_problem, daysAgo(10), daysAgo(2), "WEB");

        // Server 3 (Neutral)
        Rental rental4 = new Rental(4L, user3_bronze, server3_neutral, daysAgo(25), daysAgo(5), "WEB");

        // Server 4 (High Velocity): Many recent rentals
        Rental rental5 = new Rental(5L, user1_silver, server4_high_velocity, daysAgo(20), daysAgo(15), "DB");
        Rental rental6 = new Rental(6L, user2_gold, server4_high_velocity, daysAgo(15), daysAgo(10), "ML");
        Rental rental7 = new Rental(7L, user3_bronze, server4_high_velocity, daysAgo(10), daysAgo(5), "WEB");
        Rental rental8 = new Rental(8L, user4_none, server4_high_velocity, daysAgo(120), daysAgo(1), "DB");

        
        // Make Server 1 (Star) start with a high score
        Rating rating1 = new Rating(1L, user2_gold, server1_star, 5, daysAgo(8), rental1);
        Rating rating2 = new Rating(2L, user1_silver, server1_star, 5, daysAgo(3), rental2);

        // Make Server 2 (Problem) start with a low score
        Rating rating3 = new Rating(3L, user4_none, server2_problem, 1, daysAgo(1), rental3);

        Rating rating4 = new Rating(4L, user3_bronze, server4_high_velocity, 5, daysAgo(3), rental8);
       
        kieSession.insert(user1_silver);
        kieSession.insert(user2_gold);
        kieSession.insert(user3_bronze);
        kieSession.insert(user4_none);

        kieSession.insert(server1_star);
        kieSession.insert(server2_problem);
        kieSession.insert(server3_neutral);
        kieSession.insert(server4_high_velocity);

        kieSession.insert(rental1);
        kieSession.insert(rental2);
        kieSession.insert(rental3);
        kieSession.insert(rental4);
        kieSession.insert(rental5);
        kieSession.insert(rental6);
        kieSession.insert(rental7);
        kieSession.insert(rental8);

        kieSession.insert(rating1);
        kieSession.insert(rating2);
        kieSession.insert(rating3);
        kieSession.insert(rating4);
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

    private Date daysAgo(int days) {
        return new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days));
    }
}