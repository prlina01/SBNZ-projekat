package com.ftn.sbnz.service.config;

import com.ftn.sbnz.model.SearchFilters;
import com.ftn.sbnz.model.Server;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import org.kie.api.runtime.rule.FactHandle;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EliminationRuleLoaderRegionTest {

    @Test
    void shouldRetractServersOutsideSelectedRegion() {
        EliminationRuleLoader loader = new EliminationRuleLoader();
        KieServices kieServices = KieServices.Factory.get();
    KieContainer container = kieServices.newKieContainer(
        kieServices.newReleaseId("com.ftn.sbnz", "kjar", "0.0.1-SNAPSHOT"));

    loader.ensureEliminationRules(container.getKieBase("rules"));
    KieSession session = container.newKieSession("k-session");
        try {
            loader.ensureEliminationRules(session);

            SearchFilters filters = new SearchFilters();
            filters.setRegion(Server.Region.US);
            session.insert(filters);

            Server euServer = new Server();
            euServer.setId(42L);
            euServer.setName("EU only server");
            euServer.setRegion(Server.Region.EU);
            session.insert(euServer);

            int fired = session.fireAllRules();

            assertTrue(fired > 0, "At least one elimination rule should have fired");

            FactHandle handle = session.getFactHandle(euServer);
            assertNull(handle, "Inserted EU server fact should be retracted");

            Collection<?> remaining = session.getObjects(obj -> obj instanceof Server);
            assertEquals(0, remaining.size(), "EU server should be eliminated when US region is required");
        } finally {
            session.dispose();
        }
    }
}
