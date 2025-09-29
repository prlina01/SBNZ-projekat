package com.ftn.sbnz.service.services.catalog.impl;

import com.ftn.sbnz.model.Rental;
import com.ftn.sbnz.model.Server;
import com.ftn.sbnz.service.services.catalog.ServiceUsageInspector;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DroolsServiceUsageInspector implements ServiceUsageInspector {

    private final KieSession kieSession;

    public DroolsServiceUsageInspector(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    @Override
    public boolean isOfferingInUse(Long offeringId) {
        return kieSession.getObjects(obj -> obj instanceof Rental)
                .stream()
                .map(Rental.class::cast)
                .anyMatch(rental -> isActiveRentalForOffering(rental, offeringId));
    }

    private boolean isActiveRentalForOffering(Rental rental, Long offeringId) {
        Server server = rental.getServer();
        if (server == null || server.getId() == null) {
            return false;
        }
        return Objects.equals(server.getId(), offeringId) && rental.getEndDate() == null;
    }
}
