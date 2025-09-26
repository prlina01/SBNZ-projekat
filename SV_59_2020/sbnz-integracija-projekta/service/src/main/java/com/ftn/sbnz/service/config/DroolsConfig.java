package com.ftn.sbnz.service.config;

import com.ftn.sbnz.model.*;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Date;

@Configuration
public class DroolsConfig {

    private static final Logger log = LoggerFactory.getLogger(DroolsConfig.class);

    @Bean
    public KieSession kieSession(KieContainer kieContainer) {
        log.info("Initializing KieSession...");
        KieSession kieSession = kieContainer.newKieSession("cepKsession");

        // Insert initial facts
        User user1 = new User(1L, "user1");
        user1.setStatus(User.UserStatus.SILVER);
        User user2 = new User(2L, "user2");
        user2.setStatus(User.UserStatus.GOLD);

        Server server1 = new Server();
        server1.setId(1L);
        server1.setName("AWS EC2 m5.large");

        Server server2 = new Server();
        server2.setId(2L);
        server2.setName("GCP n2-standard-2");

        Rental rental1 = new Rental(1L, user1, server1, LocalDate.now().minusDays(40), LocalDate.now().minusDays(10), "WEB"); // 30 days
        Rental rental2 = new Rental(2L, user2, server2, LocalDate.now().minusDays(100), LocalDate.now().minusDays(5), "DB"); // 95 days

        kieSession.insert(user1);
        kieSession.insert(user2);
        kieSession.insert(server1);
        kieSession.insert(server2);
        kieSession.insert(rental1);
        kieSession.insert(rental2);

        log.info("KieSession initialized and initial facts inserted.");
        return kieSession;
    }
}
