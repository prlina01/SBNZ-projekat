package com.ftn.sbnz.service.services.catalog;

import com.ftn.sbnz.model.SearchFilters;
import com.ftn.sbnz.model.Server;
import com.ftn.sbnz.model.User;
import com.ftn.sbnz.model.catalog.ServiceOffering;
import com.ftn.sbnz.service.catalog.dto.RecommendationRequest;
import com.ftn.sbnz.service.catalog.dto.ServiceOfferingResponse;
import com.ftn.sbnz.service.repositories.ServiceOfferingRepository;
import com.ftn.sbnz.service.repositories.UserRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RecommendationService {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 25;

    private final ServiceOfferingRepository repository;
    private final ServiceOfferingMapper mapper;
    private final UserRepository userRepository;
    private final KieContainer kieContainer;

    public RecommendationService(ServiceOfferingRepository repository,
                                 ServiceOfferingMapper mapper,
                                 UserRepository userRepository,
                                 KieContainer kieContainer) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.kieContainer = kieContainer;
    }

    public List<ServiceOfferingResponse> recommend(RecommendationRequest request, String username) {
        SearchFilters filters = mapFilters(request);
        KieSession kieSession = kieContainer.newKieSession("k-session");
        try {
            kieSession.insert(filters);
            Optional<User> maybeUser = resolveUser(username);
            maybeUser.ifPresent(kieSession::insert);

            List<ServiceOffering> offerings = repository.findAll();
            Map<Long, ServiceOffering> offeringsById = new HashMap<>();
            for (ServiceOffering offering : offerings) {
                Server serverFact = offering.toServer();
                serverFact.setScore(0);
                kieSession.insert(serverFact);
                offeringsById.put(serverFact.getId(), offering);
            }

            kieSession.fireAllRules();

            int limit = resolveLimit(request.getLimit());
            return kieSession.getObjects(obj -> obj instanceof Server).stream()
                    .map(Server.class::cast)
                    .sorted(Comparator.comparingInt(Server::getScore).reversed()
                            .thenComparing(Server::getPricePerMonth))
                    .limit(limit)
                    .map(server -> buildResponse(server, offeringsById.get(server.getId()), filters))
                    .collect(Collectors.toList());
        } finally {
            kieSession.dispose();
        }
    }

    private Optional<User> resolveUser(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username);
    }

    private ServiceOfferingResponse buildResponse(Server server,
                                                  ServiceOffering offering,
                                                  SearchFilters filters) {
        double normalizedScore = normalizeScore(server.getScore());
        List<String> highlights = buildHighlights(server, filters);
        List<String> warnings = buildWarnings(server, filters);
        if (offering == null) {
            ServiceOfferingResponse fallback = new ServiceOfferingResponse();
            fallback.setId(server.getId());
            fallback.setName(server.getName());
            if (server.getProvider() != null) {
                fallback.setProvider(server.getProvider().getName());
            }
            fallback.setPurpose(server.getPurpose());
            fallback.setCpuPerformance(server.getCpuPerformance());
            fallback.setVcpuCount(server.getVcpuCount());
            fallback.setGpuModel(server.getGpuModel());
            fallback.setGpuVram(server.getGpuVram());
            fallback.setRam(server.getRam());
            fallback.setStorageCapacity(server.getStorageCapacity());
            fallback.setStorageType(server.getStorageType());
            fallback.setEncryptedStorage(server.isEncryptedStorage());
            fallback.setNetworkBandwidth(server.getNetworkBandwidth());
            fallback.setDdosProtection(server.isDdosProtection());
            fallback.setHighAvailability(server.isHighAvailability());
            fallback.setRegion(server.getRegion());
            fallback.setEcoFriendly(server.isEcoFriendly());
            fallback.setPricePerHour(server.getPricePerHour());
            fallback.setPricePerMonth(server.getPricePerMonth());
            fallback.setMatchScore(normalizedScore);
            fallback.setHighlights(highlights);
            fallback.setWarnings(warnings);
            return fallback;
        }
        return mapper.toResponse(offering, normalizedScore, highlights, warnings);
    }

    private SearchFilters mapFilters(RecommendationRequest request) {
        SearchFilters filters = new SearchFilters();
        if (request == null) {
            return filters;
        }
        filters.setPurpose(request.getPurpose());
        filters.setCpuPerformance(request.getCpuPerformance());
        filters.setMinVcpuCount(safeInt(request.getMinVcpuCount()));
        filters.setGpuRequired(Boolean.TRUE.equals(request.getGpuRequired()));
        filters.setMinGpuVram(safeInt(request.getMinGpuVram()));
        filters.setMinRam(safeInt(request.getMinRam()));
        filters.setMinStorageCapacity(safeInt(request.getMinStorageCapacity()));
        filters.setStorageType(request.getStorageType());
        filters.setEncryptedStorage(Boolean.TRUE.equals(request.getEncryptedStorage()));
        filters.setMinNetworkBandwidth(safeInt(request.getMinNetworkBandwidth()));
        filters.setDdosProtection(Boolean.TRUE.equals(request.getDdosProtection()));
        filters.setHighAvailability(Boolean.TRUE.equals(request.getHighAvailability()));
        filters.setRegion(request.getRegion());
        filters.setEcoPriority(Boolean.TRUE.equals(request.getEcoPriority()));
        filters.setConcurrentUsers(safeInt(request.getConcurrentUsers()));
        filters.setBudget(request.getBudget());
        filters.setRentalDuration(safeInt(request.getRentalDuration()));
        return filters;
    }

    private int safeInt(Integer value) {
        return value != null ? Math.max(0, value) : 0;
    }

    private int resolveLimit(Integer limit) {
        if (limit == null || limit < 1) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private double normalizeScore(int score) {
        int bounded = Math.max(0, Math.min(score, 100));
        return bounded;
    }

    private List<String> buildHighlights(Server server, SearchFilters filters) {
        List<String> highlights = new ArrayList<>();
        if (filters.getPurpose() != null && server.getPurpose() == filters.getPurpose()) {
            highlights.add("Optimized for " + humanize(filters.getPurpose()));
        }
        if (filters.isGpuRequired() && server.getGpuModel() != Server.GpuModel.NONE) {
            highlights.add("Dedicated " + humanize(server.getGpuModel()) + " GPU");
        }
        if (filters.getMinRam() > 0 && server.getRam() >= filters.getMinRam()) {
            highlights.add(server.getRam() + " GB RAM ready");
        }
        if (filters.getMinStorageCapacity() > 0 && server.getStorageCapacity() >= filters.getMinStorageCapacity()) {
            highlights.add(server.getStorageCapacity() + " GB " + humanize(server.getStorageType()));
        }
        if (filters.isEcoPriority() && server.isEcoFriendly()) {
            highlights.add("Eco friendly datacenter");
        }
        if (filters.getMinNetworkBandwidth() > 0 && server.getNetworkBandwidth() >= filters.getMinNetworkBandwidth()) {
            highlights.add(server.getNetworkBandwidth() + " Mbps bandwidth");
        }
        if (filters.getBudget() != null && server.getPricePerMonth() > 0) {
            double price = server.getPricePerMonth();
            if (filters.getBudget() == SearchFilters.Budget.LOW && price <= 80) {
                highlights.add("Budget friendly monthly plan");
            } else if (filters.getBudget() == SearchFilters.Budget.MEDIUM && price <= 180) {
                highlights.add("Balanced cost/performance");
            } else if (filters.getBudget() == SearchFilters.Budget.HIGH && price >= 250) {
                highlights.add("Premium performance tier");
            }
        }
        if (filters.getRegion() != null && server.getRegion() == filters.getRegion()) {
            highlights.add("Region: " + humanize(server.getRegion()));
        }
        if (filters.isDdosProtection() && server.isDdosProtection()) {
            highlights.add("Built-in DDoS protection");
        }
        if (filters.isHighAvailability() && server.isHighAvailability()) {
            highlights.add("High availability SLA");
        }
        if (filters.isEncryptedStorage() && server.isEncryptedStorage()) {
            highlights.add("Encrypted storage enabled");
        }
        if (filters.getConcurrentUsers() > 0) {
            int required = bandwidthRequirement(filters.getConcurrentUsers());
            if (server.getNetworkBandwidth() >= required) {
                highlights.add("Handles ~" + filters.getConcurrentUsers() + " concurrent users");
            }
        }
        return highlights;
    }

    private List<String> buildWarnings(Server server, SearchFilters filters) {
        List<String> warnings = new ArrayList<>();
        double price = server.getPricePerMonth();
        if (filters.getBudget() == SearchFilters.Budget.LOW && price > 100) {
            warnings.add("Monthly cost may exceed low-budget expectations");
        }
        if (filters.getBudget() == SearchFilters.Budget.MEDIUM && price > 220) {
            warnings.add("Consider budget impact for medium tier");
        }
        if (filters.getConcurrentUsers() > 0) {
            int required = bandwidthRequirement(filters.getConcurrentUsers());
            if (server.getNetworkBandwidth() < required + 100) {
                warnings.add("Bandwidth headroom is limited for peak load");
            }
        }
        return warnings;
    }

    private int bandwidthRequirement(int concurrentUsers) {
        if (concurrentUsers <= 100) {
            return 200;
        }
        if (concurrentUsers <= 500) {
            return 500;
        }
        return 1000;
    }

    private String humanize(Enum<?> value) {
        if (value == null) {
            return "";
        }
        return value.name().toLowerCase(Locale.ROOT).replace('_', ' ');
    }
}
