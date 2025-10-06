package com.ftn.sbnz.service.services.catalog;

import com.ftn.sbnz.model.SearchFilters;
import com.ftn.sbnz.model.Server;
import com.ftn.sbnz.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class RecommendationServiceDiscountTest {

    private final KieContainer kieContainer = KieServices.Factory.get().getKieClasspathContainer();
    private KieSession kieSession;

    @AfterEach
    void tearDown() {
        if (kieSession != null) {
            kieSession.dispose();
            kieSession = null;
        }
    }

    @Test
    @DisplayName("Rental duration over 180 days applies 15% discount")
    void longTermRentalDiscountApplied() {
        kieSession = kieContainer.newKieSession("k-session");

        SearchFilters filters = new SearchFilters();
        filters.setRentalDuration(200);

        Server server = new Server();
        server.setId(1L);
        server.setName("Test server");
        server.setPricePerMonth(200.0);
        server.setBasePricePerMonth(200.0);
        server.setPricePerHour(1.5);
        server.setBasePricePerHour(1.5);

        kieSession.insert(filters);
        kieSession.insert(server);
        kieSession.fireAllRules();

        assertThat(server.getPricePerMonth()).isEqualTo(170.0);
        assertThat(server.getPricePerHour()).isEqualTo(1.28);
        assertThat(server.getAppliedDiscountRate()).isEqualTo(0.15d);
        assertThat(server.getRuleHighlights())
                .anyMatch(h -> h.contains("15%") && h.contains("200-day"));
    }

    @Test
    @DisplayName("Bronze users receive 5 percent loyalty discount")
    void bronzeStatusDiscountApplied() {
    kieSession = kieContainer.newKieSession("k-session");

    User user = new User();
    user.setStatus(User.UserStatus.BRONZE);

    Server server = new Server();
    server.setId(11L);
    server.setName("Bronze server");
    server.setPricePerMonth(120.0);
    server.setBasePricePerMonth(120.0);
    server.setPricePerHour(0.9);
    server.setBasePricePerHour(0.9);

    kieSession.insert(user);
    kieSession.insert(server);
    kieSession.fireAllRules();

    assertThat(server.getPricePerMonth()).isEqualTo(114.0);
    assertThat(server.getPricePerHour()).isEqualTo(0.86);
    assertThat(server.getAppliedDiscountRate()).isEqualTo(0.05d);
    assertThat(server.getRuleHighlights())
        .anyMatch(h -> h.contains("5%") && h.contains("Bronze"));
    }

    @Test
    @DisplayName("Silver loyalty discount stacks with duration discount")
    void silverDiscountCombinesWithDuration() {
    kieSession = kieContainer.newKieSession("k-session");

    SearchFilters filters = new SearchFilters();
    filters.setRentalDuration(120);

    User user = new User();
    user.setStatus(User.UserStatus.SILVER);

    Server server = new Server();
    server.setId(12L);
    server.setName("Silver combo server");
    server.setPricePerMonth(300.0);
    server.setBasePricePerMonth(300.0);
    server.setPricePerHour(2.0);
    server.setBasePricePerHour(2.0);

    kieSession.insert(filters);
    kieSession.insert(user);
    kieSession.insert(server);
    kieSession.fireAllRules();

    double expectedRate = 1d - ((1d - 0.10d) * (1d - 0.10d));
    assertThat(server.getAppliedDiscountRate()).isCloseTo(expectedRate, within(1e-9));
    assertThat(server.getPricePerMonth()).isEqualTo(243.0);
    assertThat(server.getPricePerHour()).isEqualTo(1.62);
    assertThat(server.getRuleHighlights())
        .anyMatch(h -> h.contains("10%") && h.contains("Silver"));
    assertThat(server.getRuleHighlights())
        .anyMatch(h -> h.contains("Long-term"));
    }

    @Test
    @DisplayName("Gold users receive 15 percent loyalty discount")
    void goldStatusDiscountApplied() {
        kieSession = kieContainer.newKieSession("k-session");

        User user = new User();
        user.setStatus(User.UserStatus.GOLD);

        Server server = new Server();
        server.setId(13L);
        server.setName("Gold server");
        server.setPricePerMonth(400.0);
        server.setBasePricePerMonth(400.0);
        server.setPricePerHour(3.0);
        server.setBasePricePerHour(3.0);

        kieSession.insert(user);
        kieSession.insert(server);
        kieSession.fireAllRules();

        assertThat(server.getPricePerMonth()).isEqualTo(340.0);
        assertThat(server.getPricePerHour()).isEqualTo(2.55);
        assertThat(server.getAppliedDiscountRate()).isEqualTo(0.15d);
        assertThat(server.getRuleHighlights())
                .anyMatch(h -> h.contains("15%") && h.contains("Gold"));
    }

    @Test
    @DisplayName("Rental duration at 30 days keeps base pricing")
    void shortTermRentalKeepsBasePrice() {
        kieSession = kieContainer.newKieSession("k-session");

        SearchFilters filters = new SearchFilters();
        filters.setRentalDuration(30);

        Server server = new Server();
        server.setId(2L);
        server.setName("Baseline server");
        server.setPricePerMonth(150.0);
        server.setBasePricePerMonth(150.0);
        server.setPricePerHour(0.8);
        server.setBasePricePerHour(0.8);

        kieSession.insert(filters);
        kieSession.insert(server);
        kieSession.fireAllRules();

        assertThat(server.getPricePerMonth()).isEqualTo(150.0);
        assertThat(server.getPricePerHour()).isEqualTo(0.8);
        assertThat(server.getAppliedDiscountRate()).isNull();
        assertThat(server.getRuleHighlights())
                .noneMatch(h -> h.contains("discount"));
    }
}
