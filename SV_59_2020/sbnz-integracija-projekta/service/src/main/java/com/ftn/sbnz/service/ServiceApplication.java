package com.ftn.sbnz.service;

import java.util.Arrays;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftn.sbnz.model.Provider;
import com.ftn.sbnz.model.Rental;
import com.ftn.sbnz.model.SearchFilters;
import com.ftn.sbnz.model.Server;
import com.ftn.sbnz.model.User;
import com.ftn.sbnz.model.util.KnowledgeSessionHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ServiceApplication  {
	
	private static Logger log = LoggerFactory.getLogger(ServiceApplication.class);
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(ServiceApplication.class, args);

		 // Use KnowledgeSessionHelper to get KieContainer and KieSession
		KieContainer kieContainer = KnowledgeSessionHelper.createRuleBase();
		KieSession kieSession = KnowledgeSessionHelper.getStatefulKnowledgeSession(kieContainer, "k-session");

		// Create sample data
		User user1 = new User(1L, "user1");
		User user2 = new User(2L, "user2");

		Provider provider1 = new Provider(1L, "AWS");
		Provider provider2 = new Provider(2L, "GCP");

		Server server1 = new Server();
		server1.setId(1L);
		server1.setName("AWS EC2 m5.large");
		server1.setProvider(provider1);
		server1.setEcoFriendly(true);
		server1.setDdosProtection(true);
		server1.setEncryptedStorage(false);
		server1.setPricePerMonth(100);

		Server server2 = new Server();
		server2.setId(2L);
		server2.setName("GCP n2-standard-2");
		server2.setProvider(provider2);
		server2.setEcoFriendly(false);
		server2.setDdosProtection(false);
		server2.setEncryptedStorage(true);
		server2.setPricePerMonth(120);
		
		Server server3 = new Server();
		server3.setId(3L);
		server3.setName("Azure B2s");
		server3.setProvider(new Provider(3L, "Azure"));
		server3.setEcoFriendly(true);
		server3.setDdosProtection(true);
		server3.setEncryptedStorage(true);
		server3.setPricePerMonth(80);


		List<Rental> rentals = new ArrayList<>();
		rentals.add(new Rental(1L, user1, server1, LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1)));
		rentals.add(new Rental(2L, user1, server2, LocalDate.now().minusWeeks(3), LocalDate.now().minusWeeks(1)));
		rentals.add(new Rental(3L, user1, server1, LocalDate.now().minusDays(10), LocalDate.now().minusDays(1)));
		user1.setRentals(rentals);

		SearchFilters filters = new SearchFilters();
		filters.setEcoPriority(true);
		filters.setDdosProtection(true);
		filters.setEncryptedStorage(true);
		filters.setBudget(SearchFilters.Budget.LOW);


		// Insert into session
		kieSession.insert(user1);
		kieSession.insert(user2);
		for (Rental rental : rentals) {
			kieSession.insert(rental);
		}
		kieSession.insert(server1);
		kieSession.insert(server2);
		kieSession.insert(server3);
		kieSession.insert(filters);

		// Fire rules
		System.out.println("--- Firing rules ---");
		kieSession.fireAllRules();
		System.out.println("--- Rules fired ---");

		// Print results
		System.out.println("User1 status: " + user1.getStatus());
		System.out.println("Server1 score: " + server1.getScore());
		System.out.println("Server2 score: " + server2.getScore());
		System.out.println("Server3 score: " + server3.getScore());

		kieSession.dispose();
	}

	@Bean
	public KieContainer kieContainer() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks
				.newKieContainer(ks.newReleaseId("com.ftn.sbnz", "kjar", "0.0.1-SNAPSHOT"));
		KieScanner kScanner = ks.newKieScanner(kContainer);
		kScanner.start(1000);
		return kContainer;
	}
}
