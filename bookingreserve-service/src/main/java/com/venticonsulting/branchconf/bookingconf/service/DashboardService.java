package com.venticonsulting.branchconf.bookingconf.service;

import com.venticonsulting.branchconf.bookingconf.entity.dto.BranchResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class DashboardService {

    private final WebClient dashboardServiceWebClient;

    public DashboardService(WebClient dashboardServiceWebClient) {
        this.dashboardServiceWebClient = dashboardServiceWebClient;
    }

    public BranchResponseEntity retrieveBranchResponseEntity(String branchCode){
        log.info("Retrieve branch configuration data from code {}", branchCode);

        return dashboardServiceWebClient
                .get()
                .uri("/ventimetridashboard/api/dashboard/getbranchdata",
                        uriBuilder -> uriBuilder.queryParam("branchCode", branchCode).build())
                .retrieve()
                .bodyToMono(BranchResponseEntity.class)
                .block();
    }

}
