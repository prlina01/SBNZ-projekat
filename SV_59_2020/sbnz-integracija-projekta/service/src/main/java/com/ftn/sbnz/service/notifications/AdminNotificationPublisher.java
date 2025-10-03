package com.ftn.sbnz.service.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminNotificationPublisher {

    private static final Logger log = LoggerFactory.getLogger(AdminNotificationPublisher.class);
    private static final String DESTINATION = "/topic/admin-alerts";

    private final SimpMessagingTemplate messagingTemplate;

    public AdminNotificationPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publish(AdminNotificationMessage message) {
        if (message == null) {
            return;
        }
        messagingTemplate.convertAndSend(DESTINATION, message);
        log.debug("Published admin notification for server [{}]", message.getServerId());
    }
}
