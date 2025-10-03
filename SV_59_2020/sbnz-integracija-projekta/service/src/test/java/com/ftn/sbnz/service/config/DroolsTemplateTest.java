package com.ftn.sbnz.service.config;

import org.drools.template.ObjectDataCompiler;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class DroolsTemplateTest {

    @Test
    void eliminationTemplateShouldLoadIntoKSession() {
        EliminationRuleLoader loader = new EliminationRuleLoader();
        KieServices kieServices = KieServices.Factory.get();
        KieContainer container = kieServices.newKieContainer(
                kieServices.newReleaseId("com.ftn.sbnz", "kjar", "0.0.1-SNAPSHOT"));

        loader.ensureEliminationRules(container.getKieBase("rules"));
        KieSession session = container.newKieSession("k-session");
        try {
            assertDoesNotThrow(() -> session.fireAllRules());
        } finally {
            session.dispose();
        }
    }

    @Test
    void compiledTemplateShouldContainRegionRule() {
        EliminationRuleLoader loader = new EliminationRuleLoader();

        String drl = loader.compileTemplate();

        System.out.println("Compiled elimination DRL:\n" + drl.replace("\r", "\\r\n"));
        assertTrue(drl.contains("rule \"Eliminate region mismatch\""),
                "Compiled DRL should contain region mismatch rule definition");
    }

    @Test
    void objectDataCompilerWithMapShouldProduceRule() throws Exception {
        ObjectDataCompiler compiler = new ObjectDataCompiler();

        Map<String, Object> row = new HashMap<>();
        row.put("name", "region mismatch");
        row.put("filterPattern", "SearchFilters(region != null, $region : region)");
        row.put("serverBinding", "$server");
        row.put("serverPattern", "Server(region != $region)");
        row.put("extraCondition", "eval(true)");

        String drl = compiler.compile(
                Collections.singletonList(row),
                getClass().getClassLoader().getResourceAsStream("rules/recommendation/elimination.drt")
        );

        System.out.println("Map compiled DRL:\n" + drl.replace("\r", "\\r\n"));
        assertTrue(drl.contains("rule \"Eliminate region mismatch\""));
    }

    @Test
    void loaderRuleRowsShouldProduceRule() throws Exception {
        EliminationRuleLoader loader = new EliminationRuleLoader();
        ObjectDataCompiler compiler = new ObjectDataCompiler();

        String drl = compiler.compile(
                loader.getRuleRows(),
                getClass().getClassLoader().getResourceAsStream("rules/recommendation/elimination.drt")
        );

        System.out.println("Loader rows compiled DRL:\n" + drl.replace("\r", "\\r\n"));
        assertTrue(drl.contains("rule \"Eliminate region mismatch\""));
    }
}
