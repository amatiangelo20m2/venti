package com.ventimetriconsulting.branch.configuration.bookingconf.service;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto.BranchResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class BranchConfigurationService {

    private final WebClient dashboardServiceWebClient;

    public BranchConfigurationService(WebClient dashboardServiceWebClient) {
        this.dashboardServiceWebClient = dashboardServiceWebClient;
    }

    public BranchResponseEntity retrieveBranchResponseEntity(String branchCode){
        log.info("Retrieve branch configuration data from code {}", branchCode);

        return dashboardServiceWebClient
                .get()
                .uri("/ventimetriservice/api/dashboard/getbranchdata",
                        uriBuilder -> uriBuilder.queryParam("branchCode", branchCode).build())
                .retrieve()
                .bodyToMono(BranchResponseEntity.class)
                .block();
    }

}
