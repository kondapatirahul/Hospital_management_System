package com.healthcare.hospital.enterprise.phase5.catalog;

import com.healthcare.hospital.enterprise.phase5.notification.service.NotificationItemService;
import com.healthcare.hospital.enterprise.phase5.integrationendpoint.service.IntegrationEndpointService;
import com.healthcare.hospital.enterprise.phase5.schedulewindow.service.ScheduleWindowService;
import com.healthcare.hospital.enterprise.phase5.automationrun.service.AutomationRunService;
import com.healthcare.hospital.enterprise.phase5.accessreview.service.AccessReviewService;
import com.healthcare.hospital.enterprise.phase5.retentioncase.service.RetentionCaseService;
import com.healthcare.hospital.enterprise.phase5.systemincident.service.SystemIncidentService;
import com.healthcare.hospital.enterprise.phase5.analyticssnapshot.service.AnalyticsSnapshotService;
import com.healthcare.hospital.enterprise.phase5.consentrecord.service.ConsentRecordService;
import com.healthcare.hospital.enterprise.phase5.webhookdelivery.service.WebhookDeliveryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.*;

@Controller
public class Phase5CatalogController {
    private final NotificationItemService notificationService;
    private final IntegrationEndpointService integrationendpointService;
    private final ScheduleWindowService schedulewindowService;
    private final AutomationRunService automationrunService;
    private final AccessReviewService accessreviewService;
    private final RetentionCaseService retentioncaseService;
    private final SystemIncidentService systemincidentService;
    private final AnalyticsSnapshotService analyticssnapshotService;
    private final ConsentRecordService consentrecordService;
    private final WebhookDeliveryService webhookdeliveryService;
    public Phase5CatalogController(NotificationItemService notificationService, IntegrationEndpointService integrationendpointService, ScheduleWindowService schedulewindowService, AutomationRunService automationrunService, AccessReviewService accessreviewService, RetentionCaseService retentioncaseService, SystemIncidentService systemincidentService, AnalyticsSnapshotService analyticssnapshotService, ConsentRecordService consentrecordService, WebhookDeliveryService webhookdeliveryService) {
        this.notificationService=notificationService;
        this.integrationendpointService=integrationendpointService;
        this.schedulewindowService=schedulewindowService;
        this.automationrunService=automationrunService;
        this.accessreviewService=accessreviewService;
        this.retentioncaseService=retentioncaseService;
        this.systemincidentService=systemincidentService;
        this.analyticssnapshotService=analyticssnapshotService;
        this.consentrecordService=consentrecordService;
        this.webhookdeliveryService=webhookdeliveryService;
    }
    @GetMapping("/enterprise/phase5") public String catalog(Model model) {
        Map<String,Integer> counts=new LinkedHashMap<>(); List<Map<String,String>> links=new ArrayList<>();
        counts.put("Notifications", notificationService.list().size());
        counts.put("Integration Endpoints", integrationendpointService.list().size());
        counts.put("Schedule Windows", schedulewindowService.list().size());
        counts.put("Automation Runs", automationrunService.list().size());
        counts.put("Access Reviews", accessreviewService.list().size());
        counts.put("Retention Cases", retentioncaseService.list().size());
        counts.put("System Incidents", systemincidentService.list().size());
        counts.put("Analytics Snapshots", analyticssnapshotService.list().size());
        counts.put("Consent Records", consentrecordService.list().size());
        counts.put("Webhook Deliveries", webhookdeliveryService.list().size());
        links.add(Map.of("title","Notifications","path","/enterprise/phase5/notification","description","Message delivery and acknowledgement workflows"));
        links.add(Map.of("title","Integration Endpoints","path","/enterprise/phase5/integrationendpoint","description","Connector configuration and health lifecycle"));
        links.add(Map.of("title","Schedule Windows","path","/enterprise/phase5/schedulewindow","description","Cross-team scheduling and blackout windows"));
        links.add(Map.of("title","Automation Runs","path","/enterprise/phase5/automationrun","description","Safe tracked automation execution records"));
        links.add(Map.of("title","Access Reviews","path","/enterprise/phase5/accessreview","description","Periodic least-privilege review and attestation"));
        links.add(Map.of("title","Retention Cases","path","/enterprise/phase5/retentioncase","description","Data retention and disposition approvals"));
        links.add(Map.of("title","System Incidents","path","/enterprise/phase5/systemincident","description","Operational incidents, impact and postmortems"));
        links.add(Map.of("title","Analytics Snapshots","path","/enterprise/phase5/analyticssnapshot","description","Auditable point-in-time operational metrics"));
        links.add(Map.of("title","Consent Records","path","/enterprise/phase5/consentrecord","description","Patient consent capture, review and revocation tracking"));
        links.add(Map.of("title","Webhook Deliveries","path","/enterprise/phase5/webhookdelivery","description","Integration delivery attempts and retry management"));
        model.addAttribute("counts",counts); model.addAttribute("workspaces",links); model.addAttribute("pageTitle","Enterprise Integration Hub");
        model.addAttribute("workspaceCount",links.size()); model.addAttribute("recordCount",counts.values().stream().mapToInt(Integer::intValue).sum());
        return "enterprise/phase5/catalog";
    }
}
