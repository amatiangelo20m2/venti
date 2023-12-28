package com.venticonsulting.waapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.venticonsulting.waapi.entity.WaApiConfigEntity;
import com.venticonsulting.waapi.entity.dto.WaApiConfigDTO;
import com.venticonsulting.waapi.entity.waapi.CreateUpdateResponse;
import com.venticonsulting.waapi.entity.waapi.MeResponse;
import com.venticonsulting.waapi.entity.waapi.QrCodeResponse;
import com.venticonsulting.waapi.repository.WaApiConfigRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class WhatsAppApiService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient waapiWebClientBean;
    private final WaApiConfigRepository waApiConfigRepository;

    public WhatsAppApiService(WebClient waapiWebClient, WaApiConfigRepository waApiConfigRepository) {
        this.waapiWebClientBean = waapiWebClient;
        this.waApiConfigRepository = waApiConfigRepository;
    }

    @Transactional
    public WaApiConfigDTO configureNumberForWhatsAppMessaging(String branchCode) {

        log.info("Create what's app configuration for branch with code {}" , branchCode);
        Optional<WaApiConfigEntity> branchByCode = waApiConfigRepository.findAllByBranchCode(branchCode);

        if(branchByCode.isEmpty()){
            log.info("There no configuration found for branch with code {}, let's create a brand new one.." , branchCode);
            CreateUpdateResponse createUpdateResponse = createInstance();
            haveSomeTimeToSleep(1000);

            MeResponse meResponse;
            int maxIterations = 10;
            int currentIteration = 0;
            do {
                log.info("Retrieve a new created client info from waapi server untill it goes in 'qr' status. The qr code will be send to the client to configure the what'app app on a phone");

                meResponse = retrieveClientInfo(createUpdateResponse.getInstance().getId());

                if (meResponse != null
                        && !"success".equals(meResponse.getStatus())) {
                    haveSomeTimeToSleep(1000);
                }

                currentIteration++;

            } while (meResponse == null ||
                    (!"success".equals(meResponse.getStatus()) ||
                            !"error".equals(meResponse.getMe().getStatus()) ||
                            !"qr".equals(meResponse.getMe().getInstanceStatus())) &&
                            currentIteration < maxIterations);


            log.info("At this time the status is QR so is ready to give back the Image Code");
            log.info("Call the method to retrieve the qr code for this instance id {}", createUpdateResponse.getInstance().getId() );

            QrCodeResponse qrCodeResponse = retrieveQrCode(createUpdateResponse.getInstance().getId());


            WaApiConfigEntity waConfig = waApiConfigRepository.save(WaApiConfigEntity.builder()
                    .branchCode(branchCode)
                    .waapiConfId(0)
                    .owner(createUpdateResponse.getInstance().getOwner())
                    .displayName("")
                    .formattedNumber("")
                    .instanceStatus(meResponse.getMe().getInstanceStatus())
                    .creationDate(new Date())
                    .instanceId(createUpdateResponse.getInstance().getId())
                    .lastQrCode(qrCodeResponse.getQrCode().getData().getQrCode())
                    .profilePicUrl("")
                    .message(meResponse.getMe().getMessage())
                    .explanation(meResponse.getMe().getExplanation())
                    .build());

            return WaApiConfigDTO.fromEntity(waConfig);
        }else {
            // retrieve status and if is in qr code try to get the qr code
            log.info("Retrieve status of waapi instance and if is in qr code try to get a new qr code to configure branch with code {}", branchCode);
            MeResponse meResponse = retrieveClientInfo(branchByCode.get().getInstanceId());

            if("success".equalsIgnoreCase(meResponse.getStatus())
                    && "error".equalsIgnoreCase(meResponse.getMe().getStatus())){
                QrCodeResponse qrCodeResponse = retrieveQrCode(branchByCode.get().getInstanceId());
                branchByCode.get().setLastQrCode(qrCodeResponse.getQrCode().getData().getQrCode());
                branchByCode.get().setUpdateDate(new Date());
            }

            return WaApiConfigDTO.fromEntity(branchByCode.get());
        }
    }


    public CreateUpdateResponse createInstance(){
        log.info("Calling /api/v1/instances method to create a waapi instance..");

        return waapiWebClientBean.post()
                .uri("/api/v1/instances")
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new Exception("Client Error"))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new Exception("Server Error"))
                )
                .bodyToMono(String.class)
                .map(responseBody -> {
                    log.info("Response from [/api/v1/instances] wa api : {}", responseBody);
                    haveSomeTimeToSleep(500);
                    try {
                        return objectMapper.readValue(responseBody, CreateUpdateResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .block();
    }





    public MeResponse retrieveClientInfo(String instanceCode) {
        log.info("Retrieve client info for instance {}", instanceCode);

        return waapiWebClientBean
                .get()
                .uri("api/v1/instances/" + instanceCode +"/client/me")
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new Exception("Client Error"))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new Exception("Server Error"))
                )
                .bodyToMono(String.class)
                .map(responseBody -> {

                    log.info("Response from [api/v1/instances/{}/client/me] wa api: {}", instanceCode, responseBody);
                    haveSomeTimeToSleep(500);
                    try {
                        return objectMapper.readValue(responseBody, MeResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .block();
    }

    public QrCodeResponse retrieveQrCode(String instanceId) {
        log.info("Retrieve QR code for a instance {}" , instanceId);

        return waapiWebClientBean
                .get()
                .uri("api/v1/instances/" + instanceId +"/client/qr")
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new Exception("Client Error"))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new Exception("Server Error"))
                )
                .bodyToMono(String.class)
                .map(responseBody -> {

                    log.info("Response from [api/v1/instances/{}/client/me] wa api: {}", instanceId, responseBody);
                    haveSomeTimeToSleep(500);
                    try {
                        return objectMapper.readValue(responseBody, QrCodeResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .block();
    }


    public void deleteInstance(String instanceCode) {
        waapiWebClientBean.delete()
                .uri("/api/v1/instances/" + instanceCode)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new Exception("Client Error"))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new Exception("Server Error"))
                )
                .bodyToMono(String.class)
                .subscribe(responseBody -> {
                    log.info("Method to delete isntance with id {} called. Response: {}", instanceCode, responseBody);
                });
    }

    private void rebootInstance(String instanceId) {
        waapiWebClientBean.delete()
                .uri("/api/v1/instances/" + instanceId + "/client/action/reboot")
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new Exception("Client Error"))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new Exception("Server Error"))
                )
                .bodyToMono(String.class)
                .subscribe(responseBody -> {
                    log.info("Method to reboot isntance with id {} called. Response: {}", instanceId, responseBody);
                });
    }

    private void haveSomeTimeToSleep(int sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            log.warn("Sleep time between creation instance on waapi server not working. Nothing bad actually, the process can be go on");
        }
    }

    @Transactional
    public WaApiConfigDTO checkWaApiStatus(String branchCode) {

        log.info("Retrieve waapi configuration for branch with code {}" , branchCode);

        Optional<WaApiConfigEntity> branchByCode = waApiConfigRepository.findAllByBranchCode(branchCode);

        if(branchByCode.isPresent()) {

            MeResponse meResponse = retrieveClientInfo(branchByCode.get().getInstanceId());

            if("success".equalsIgnoreCase(meResponse.getStatus())
                    && "success".equalsIgnoreCase(meResponse.getMe().getStatus())){
                branchByCode.get().setInstanceId(meResponse.getMe().getInstanceId());
                branchByCode.get().setFormattedNumber(meResponse.getMe().getData().getFormattedNumber());
                branchByCode.get().setDisplayName(meResponse.getMe().getData().getDisplayName());
                branchByCode.get().setProfilePicUrl(meResponse.getMe().getData().getProfilePicUrl());
                branchByCode.get().setInstanceStatus("OK");
                branchByCode.get().setExplanation("");
                branchByCode.get().setMessage("");
                branchByCode.get().setLastQrCode("");
                branchByCode.get().setUpdateDate(new Date());
            }

            return WaApiConfigDTO.fromEntity(branchByCode.get());
        }
        return null;
    }

    @Transactional
    public WaApiConfigDTO reboot(String branchCode) {
        log.info("Reboot waapi configuration for branch with code {}" , branchCode);

        Optional<WaApiConfigEntity> branchByCode = waApiConfigRepository.findAllByBranchCode(branchCode);

        if(branchByCode.isPresent()) {

            rebootInstance(branchByCode.get().getInstanceId());
            haveSomeTimeToSleep(4000);

            MeResponse meResponse = retrieveClientInfo(branchByCode.get().getInstanceId());

            if("success".equalsIgnoreCase(meResponse.getStatus())
                    && "success".equalsIgnoreCase(meResponse.getMe().getStatus())){
                branchByCode.get().setInstanceId(meResponse.getMe().getInstanceId());
                branchByCode.get().setFormattedNumber(meResponse.getMe().getData().getFormattedNumber());
                branchByCode.get().setDisplayName(meResponse.getMe().getData().getDisplayName());
                branchByCode.get().setProfilePicUrl(meResponse.getMe().getData().getProfilePicUrl());
                branchByCode.get().setInstanceStatus("OK");
                branchByCode.get().setExplanation("");
                branchByCode.get().setMessage("");
                branchByCode.get().setLastQrCode("");
                branchByCode.get().setUpdateDate(new Date());
            }

            return WaApiConfigDTO.fromEntity(branchByCode.get());
        }
        return null;
    }


}
