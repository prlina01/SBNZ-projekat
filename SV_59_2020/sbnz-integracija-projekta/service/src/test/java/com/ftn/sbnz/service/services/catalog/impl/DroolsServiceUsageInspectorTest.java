package com.ftn.sbnz.service.services.catalog.impl;

import com.ftn.sbnz.model.Rental;
import com.ftn.sbnz.model.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.runtime.KieSession;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DroolsServiceUsageInspectorTest {

    @Mock
    private KieSession kieSession;

    private DroolsServiceUsageInspector inspector;

    @BeforeEach
    void setUp() {
        inspector = new DroolsServiceUsageInspector(kieSession);
    }

    @Test
    void returnsTrueWhenActiveRentalExistsForOffering() {
        Rental activeRental = createRental(42L, null);
    when(kieSession.getObjects(any())).thenAnswer(invocation -> Collections.singletonList(activeRental));

        boolean inUse = inspector.isOfferingInUse(42L);

        assertThat(inUse).isTrue();
    }

    @Test
    void returnsFalseWhenOnlyCompletedRentalExists() {
        Rental completedRental = createRental(42L, new Date());
    when(kieSession.getObjects(any())).thenAnswer(invocation -> Collections.singletonList(completedRental));

        boolean inUse = inspector.isOfferingInUse(42L);

        assertThat(inUse).isFalse();
    }

    @Test
    void returnsFalseWhenNoRentalMatchesOffering() {
        Rental activeRental = createRental(7L, null);
    when(kieSession.getObjects(any())).thenAnswer(invocation -> Collections.singletonList(activeRental));

        boolean inUse = inspector.isOfferingInUse(42L);

        assertThat(inUse).isFalse();
    }

    @Test
    void returnsFalseWhenNoRentalsPresent() {
    when(kieSession.getObjects(any())).thenAnswer(invocation -> Collections.emptyList());

        boolean inUse = inspector.isOfferingInUse(42L);

        assertThat(inUse).isFalse();
    }

    private Rental createRental(Long serverId, Date endDate) {
        Server server = new Server();
        server.setId(serverId);

        Rental rental = new Rental();
        rental.setServer(server);
        rental.setEndDate(endDate);
        return rental;
    }

}
