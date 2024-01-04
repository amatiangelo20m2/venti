package com.venticonsulting.waapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.venticonsulting.waapi.entity.waapi.CreateUpdateResponse;
import com.venticonsulting.waapi.entity.waapi.MeResponse;
import com.venticonsulting.waapi.entity.waapi.QrCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class WaApiService {
    private final WebClient waapiWebClientBean;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WaApiService(WebClient waapiWebClientBean) {
        this.waapiWebClientBean = waapiWebClientBean;
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

    public void rebootInstance(String instanceId) {
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
}
