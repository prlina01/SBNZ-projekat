package com.ftn.sbnz.service.services.catalog;

import com.ftn.sbnz.model.SearchFilters;
import com.ftn.sbnz.service.catalog.dto.ServiceOfferingResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationServiceDiscountTest {

    @Test
    @DisplayName("Duration thresholds map to correct discount rates")
    void resolveDurationDiscountMatchesDocumentation() {
        assertThat(RecommendationService.resolveDurationDiscount(0)).isZero();
        assertThat(RecommendationService.resolveDurationDiscount(30)).isZero();
        assertThat(RecommendationService.resolveDurationDiscount(31)).isEqualTo(0.05d);
        assertThat(RecommendationService.resolveDurationDiscount(120)).isEqualTo(0.10d);
        assertThat(RecommendationService.resolveDurationDiscount(181)).isEqualTo(0.15d);
    }

    @Test
    @DisplayName("Discount application adjusts prices and adds highlight")
    void applyDurationDiscountUpdatesResponse() throws Exception {
        RecommendationService service = new RecommendationService(null, null, null, null, null);

        ServiceOfferingResponse response = new ServiceOfferingResponse();
        response.setPricePerMonth(200.0);
        response.setPricePerHour(1.5);

        SearchFilters filters = new SearchFilters();
        filters.setRentalDuration(200);

        Method method = RecommendationService.class.getDeclaredMethod(
                "applyDurationDiscount",
                ServiceOfferingResponse.class,
                double.class,
                double.class,
                SearchFilters.class
        );
        method.setAccessible(true);
        method.invoke(service, response, 200.0, 1.5, filters);

        assertThat(response.getPricePerMonth()).isEqualTo(170.0);
        assertThat(response.getPricePerHour()).isEqualTo(1.28);
        assertThat(response.getHighlights())
                .anyMatch(h -> h.contains("15%") && h.contains("200"));
    }
}
