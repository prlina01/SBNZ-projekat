package com.ftn.sbnz.service.services.catalog;

import com.ftn.sbnz.model.SearchFilters;
import com.ftn.sbnz.model.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

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
