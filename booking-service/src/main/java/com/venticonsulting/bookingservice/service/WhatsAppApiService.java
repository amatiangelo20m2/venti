package com.venticonsulting.bookingservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class WhatsAppApiService {

    private final WebClient waapiWebClient;

    public WhatsAppApiService(WebClient waapiWebClient) {
        this.waapiWebClient = waapiWebClient;
    }

    public void createInstance() {
        waapiWebClient.post()
                .uri("/api/v1/instances")
                .retrieve()
//                .onStatus(
//                        status -> HttpStatus.FAMILY_4XX.equals(status.family()),
//                        clientResponse -> Mono.error(new CustomException("Client Error"))
//                )
//                .onStatus(
//                        status -> HttpStatus.FAMILY_5XX.equals(status.family()),
//                        clientResponse -> Mono.error(new CustomException("Server Error"))
//                )
                .bodyToMono(String.class)
                .subscribe(responseBody -> {
                    System.out.println(responseBody);
                });
    }

    public void deleteInstance(String instanceCode) {
        waapiWebClient.delete()
                .uri("/api/v1/instances/" + instanceCode)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(responseBody -> {
                    System.out.println(responseBody);
                });
    }

    public void retrieveIntanceStatus(String instanceCode) {
        //
        waapiWebClient
                .get()
                .uri("api/v1/instances/" + instanceCode +"/client/status")
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(responseBody -> {
                    System.out.println(responseBody);
                });

    }

    public void retrieveClientInfo(String instanceCode) {

        waapiWebClient
                .get()
                .uri("api/v1/instances/" + instanceCode +"/client/me")
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(responseBody -> {
                    String upperCase = responseBody.toUpperCase();
                    log.info(responseBody);
                });
    }

    public void retrieveQrCode(String instanceId) {
        waapiWebClient
                .get()
                .uri("api/v1/instances/" + instanceId +"/client/qr")
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(responseBody -> {
                    String upperCase = responseBody.toUpperCase();
                    log.info(responseBody);
                });
    }
}
