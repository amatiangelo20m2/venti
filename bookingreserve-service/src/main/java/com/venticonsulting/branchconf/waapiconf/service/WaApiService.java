package com.venticonsulting.branchconf.waapiconf.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.venticonsulting.branchconf.waapiconf.dto.CreateUpdateResponse;
import com.venticonsulting.branchconf.waapiconf.dto.MeResponse;
import com.venticonsulting.branchconf.waapiconf.dto.ProfilePicResponse;
import com.venticonsulting.branchconf.waapiconf.dto.QrCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class WaApiService {

    private final WebClient waapiWebClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public WaApiService(WebClient waapiWebClient) {
        this.waapiWebClient = waapiWebClient;
    }

    public CreateUpdateResponse createInstance(){


        log.info("Calling /api/v1/instances method to create a waapi instance..");

        return waapiWebClient.post()
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

        return waapiWebClient
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

        return waapiWebClient
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
        waapiWebClient.delete()
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
        waapiWebClient.delete()
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

    public void sendMessage(String instanceId, String phone, String messageToSend) {
        // https://waapi.app/api/v1/instances/{id}/client/action/send-message

        String body = "{\"chatId\":\"PHONE@c.us\",\"message\":\"MESSAGE_PLACEHOLDER\"}";

        String refactoredJsonBody = body.replace("PHONE", phone).replace("MESSAGE_PLACEHOLDER", messageToSend);
        log.info("Send message {} to number {} by intace id {} - Body {}", messageToSend, phone, instanceId, refactoredJsonBody );

        waapiWebClient.post()
                .uri("/api/v1/instances/" + instanceId + "/client/action/send-message")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(refactoredJsonBody), refactoredJsonBody.getClass())
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
                .log("waapiWebClient.post")
                .subscribe(responseBody -> {
                    log.info("Message sent successfully to {} by instance with id {}. Response: {}", phone, instanceId, responseBody);
                });

    }

    public String retrievePhoto(String instanceId, String phone) {
        String body = "{\"contactId\":\"PHONE@c.us\"}";
        String refactoredJsonBody = body.replace("PHONE", phone);
        log.info("Retrieve image for number {} by instance id {}", phone, instanceId);

        return waapiWebClient.post()
                .uri("/api/v1/instances/" + instanceId + "/client/action/get-profile-pic-url")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(refactoredJsonBody), refactoredJsonBody.getClass())
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
                .log("waapiWebClient.post")
                .map(responseBody -> {
                    log.info("Image retrieved successfully for number {} by instance with id {}. Response: {}", phone, instanceId, responseBody);
                    try {
                        ProfilePicResponse profilePicResponse = objectMapper.readValue(responseBody, ProfilePicResponse.class);
                        return profilePicResponse.getData().getProfileData().getProfilePicUrl();
                    } catch (JsonProcessingException e) {
                        return "Unable to get image";
                    }
                }).block();
    }

    private void haveSomeTimeToSleep(int sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            log.warn("Sleep time between creation instance on waapi server not working. Nothing bad actually, the process can be go on");
        }
    }

    //TODO : same than the method that call it - chatId format wrong (check this part for future integration of massive sending message) - see sendMessage method to fix it
//    public void sendMessageWithImage(String instanceId, List<String> numbers, String urlMedia, String imageName, String message){
//
//        String body = String.format("{\"chatId\":\"%s\",\"mediaUrl\":\"%s\",\"mediaCaption\":\"%s\",\"mediaName\":\"%s\"}",
//                numbers.get(0),
//                urlMedia,
//                message,
//                imageName);
//
//        log.info("Send message : {}", body);
//
//        waapiWebClientBean.post()
//                .uri("/api/v1/instances/" + instanceId + "/client/action/send-media")
//                .body(Mono.just("{\n" +
//                        "    \"mediaUrl\": \"http://20m2official.it/wp-content/uploads/2024/01/WhatsApp-Image-2024-01-16-at-17.01.42.jpeg\",\n" +
//                        "    \"chatId\": \"393454937047@c.us\",\n" +
//                        "    \"mediaCaption\": \"Giovedì sera si accendono le luci a Monopoli!  \uD83C\uDFB8\uD83C\uDFA4\\nPreparati a vivere l'emozione del rock puro con la cover band più esplosiva dei Guns N' Rose. Rivivi i classici che hanno fatto la storia del rock in una serata indimenticabile. \\n\\n \uD83D\uDD25 Lasciati travolgere da hit come \\\" Sweet Child O ' Mine\\\" e \\\"November Rain\\\" in una location mozzafiato!  \\n\uD83D\uDCCD Dove? Monopoli, il cuore pulsante del rock. \\n\\n⏰ Quando? Giovedì sera - L' esperienza rock inizia quando cala il sole!La Cover Band dei Guns n’ Roses ti aspetta per una notte di pura adrenalina rock!\\n\\nPRENOTA IL TUO TAVOLO: https: //20m2official.it/bookingmonopoli\",\n" +
//                        "    \"mediaName\": \"AG31194-scaled.jpg\"\n" +
//                        "}"), String.class)
//                .retrieve()
//                .onStatus(
//                        HttpStatusCode::is4xxClientError,
//                        clientResponse -> Mono.error(new Exception("Client Error"))
//                )
//                .onStatus(
//                        HttpStatusCode::is5xxServerError,
//                        clientResponse -> Mono.error(new Exception("Server Error"))
//                )
//                .bodyToMono(String.class)
//                .subscribe(responseBody -> {
//                    log.info("Method send message failed for instance with id {}. Response: {}", instanceId, responseBody);
//                });
//    }

}
