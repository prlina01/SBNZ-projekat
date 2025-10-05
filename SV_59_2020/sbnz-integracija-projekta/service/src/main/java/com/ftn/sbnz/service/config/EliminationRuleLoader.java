package com.ftn.sbnz.service.config;

import com.ftn.sbnz.model.SearchFilters;
import com.ftn.sbnz.model.Server;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.io.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EliminationRuleLoader {

    private static final Logger log = LoggerFactory.getLogger(EliminationRuleLoader.class);

    private final KieServices kieServices = KieServices.Factory.get();
    private final Resource templateResource = kieServices.getResources()
            .newClassPathResource("rules/recommendation/elimination.drt");
    private final List<Map<String, Object>> ruleRows = buildEliminationRuleRows();
    private final Set<Integer> initializedKnowledgeBases = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void ensureEliminationRules(KieSession session) {
        ensureEliminationRules(session.getKieBase());
    }

    public void ensureEliminationRules(KieBase kieBase) {
        if (!(kieBase instanceof InternalKnowledgeBase)) {
            throw new IllegalStateException("Unsupported KieBase implementation: " + kieBase.getClass().getName());
        }

        InternalKnowledgeBase internalBase = (InternalKnowledgeBase) kieBase;
        int identity = System.identityHashCode(internalBase);
        if (initializedKnowledgeBases.contains(identity)) {
            return;
        }

        synchronized (internalBase) {
            if (initializedKnowledgeBases.contains(identity)) {
                return;
            }

            String drl = compileTemplate();
            KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            builder.add(
                    kieServices.getResources().newByteArrayResource(drl.getBytes(StandardCharsets.UTF_8)),
                    ResourceType.DRL
            );

            if (builder.hasErrors()) {
                throw new IllegalStateException("Errors while building elimination rules: " + builder.getErrors());
            }

            for (KiePackage kiePackage : builder.getKnowledgePackages()) {
                for (Rule rule : kiePackage.getRules()) {
                    log.info("Registering elimination rule: {}", rule.getName());
                }
            }

            for (KiePackage kiePackage : builder.getKnowledgePackages()) {
                internalBase.addPackage(kiePackage);
            }

            initializedKnowledgeBases.add(identity);
            log.info("Loaded {} elimination rules from template into KieBase.", ruleRows.size());
        }
    }

    String compileTemplate() {
        try (InputStream templateStream = templateResource.getInputStream()) {
            ObjectDataCompiler compiler = new ObjectDataCompiler();
            String drl = compiler.compile(ruleRows, templateStream);
            log.info("Compiled elimination DRL:\n{}", drl);
            return drl;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load elimination template from kjar.", e);
        }
    }

    List<Map<String, Object>> getRuleRows() {
        return Collections.unmodifiableList(ruleRows);
    }

    private List<Map<String, Object>> buildEliminationRuleRows() {
    List<Map<String, Object>> rows = new ArrayList<>();
    rows.add(row("insufficient CPU",
        "SearchFilters(cpuPerformance != null, $cpu : cpuPerformance)",
        "$server",
        "Server()",
        "eval($server.getCpuPerformance().ordinal() < $cpu.ordinal())"));
    rows.add(row("insufficient vCPU",
        "SearchFilters(minVcpuCount > 0, $min : minVcpuCount)",
        "$server",
        "Server(vcpuCount < $min)",
        "eval(true)"));
    rows.add(row("missing GPU when required",
        "SearchFilters(gpuRequired == true)",
        "$server",
        "Server(gpuModel == Server.GpuModel.NONE)",
        "eval(true)"));
    rows.add(row("low GPU memory",
        "SearchFilters(gpuRequired == true, minGpuVram > 0, $min : minGpuVram)",
        "$server",
        "Server(gpuModel != Server.GpuModel.NONE, gpuVram < $min)",
        "eval(true)"));
    rows.add(row("insufficient RAM",
        "SearchFilters(minRam > 0, $min : minRam)",
        "$server",
        "Server(ram < $min)",
        "eval(true)"));
    rows.add(row("insufficient storage",
        "SearchFilters(minStorageCapacity > 0, $min : minStorageCapacity)",
        "$server",
        "Server(storageCapacity < $min)",
        "eval(true)"));
    rows.add(row("storage type mismatch",
        "SearchFilters(storageType != null, $type : storageType)",
        "$server",
        "Server(storageType != $type)",
        "eval(true)"));
    rows.add(row("missing encryption",
        "SearchFilters(encryptedStorage == true)",
        "$server",
        "Server(encryptedStorage == false)",
        "eval(true)"));
    rows.add(row("missing DDoS protection",
        "SearchFilters(ddosProtection == true)",
        "$server",
        "Server(ddosProtection == false)",
        "eval(true)"));
    rows.add(row("missing high availability",
        "SearchFilters(highAvailability == true)",
        "$server",
        "Server(highAvailability == false)",
        "eval(true)"));
    rows.add(row("region mismatch",
        "SearchFilters(region != null, $region : region)",
        "$server",
        "Server(region != $region)",
        "eval(true)"));
    rows.add(row("insufficient bandwidth",
        "SearchFilters(minNetworkBandwidth > 0, $min : minNetworkBandwidth)",
        "$server",
        "Server(networkBandwidth < $min)",
        "eval(true)"));
    rows.add(row("insufficient throughput",
        "SearchFilters(concurrentUsers > 0, $users : concurrentUsers)",
        "$server",
        "Server()",
        "eval($server.getNetworkBandwidth() < ($users <= 100 ? 200 : ($users <= 500 ? 500 : 1000)))"));
    rows.add(row("over budget (low)",
        "SearchFilters(budget == SearchFilters.Budget.LOW)",
        "$server",
        "Server(pricePerMonth > 120)",
        "eval(true)"));
    rows.add(row("over budget (medium)",
        "SearchFilters(budget == SearchFilters.Budget.MEDIUM)",
        "$server",
        "Server(pricePerMonth > 280)",
        "eval(true)"));
    rows.add(row("non-eco server when priority",
        "SearchFilters(ecoPriority == true)",
        "$server",
        "Server(ecoFriendly == false)",
        "eval(true)"));
    return rows;
    }

    private Map<String, Object> row(String name, String filterPattern, String serverBinding,
                     String serverPattern, String extraCondition) {
    Map<String, Object> map = new HashMap<>();
    map.put("name", name);
    map.put("filterPattern", filterPattern);
    map.put("serverBinding", serverBinding);
    map.put("serverPattern", serverPattern);
    map.put("extraCondition", extraCondition);
    return map;
    }
}
