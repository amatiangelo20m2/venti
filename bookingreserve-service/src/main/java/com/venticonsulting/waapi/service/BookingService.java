package com.venticonsulting.waapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.venticonsulting.waapi.entity.BranchTimeRange;
import com.venticonsulting.waapi.entity.RestaurantConfiguration;
import com.venticonsulting.waapi.entity.TimeRange;
import com.venticonsulting.waapi.entity.WaApiConfigEntity;
import com.venticonsulting.waapi.entity.dto.BookingConfigurationDTO;
import com.venticonsulting.waapi.entity.dto.BranchTimeRangeDTO;
import com.venticonsulting.waapi.entity.dto.RestaurantOpeningConfigurationDTO;
import com.venticonsulting.waapi.entity.dto.WaApiConfigDTO;
import com.venticonsulting.waapi.entity.waapi.CreateUpdateResponse;
import com.venticonsulting.waapi.entity.waapi.MeResponse;
import com.venticonsulting.waapi.entity.waapi.QrCodeResponse;
import com.venticonsulting.waapi.repository.BranchTimeRangeRepository;
import com.venticonsulting.waapi.repository.RestaurantConfigurationRepository;
import com.venticonsulting.waapi.repository.WaApiConfigRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookingService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient waapiWebClientBean;
    private final WaApiConfigRepository waApiConfigRepository;
    private final RestaurantConfigurationRepository restaurantConfigurationRepository;

    private final BranchTimeRangeRepository branchTimeRangeRepository;

    public BookingService(WebClient waapiWebClient,
                          WaApiConfigRepository waApiConfigRepository,
                          RestaurantConfigurationRepository restaurantConfigurationRepository,
                          BranchTimeRangeRepository branchTimeRangeRepository
    ) {
        this.waapiWebClientBean = waapiWebClient;
        this.waApiConfigRepository = waApiConfigRepository;
        this.restaurantConfigurationRepository = restaurantConfigurationRepository;
        this.branchTimeRangeRepository = branchTimeRangeRepository;
    }

    @Transactional
    public BookingConfigurationDTO configureNumberForWhatsAppMessaging(String branchCode) {

        log.info("Create what's app configuration for branch with code {}" , branchCode);
        Optional<RestaurantConfiguration> restaurantConfByBranchCode = restaurantConfigurationRepository.findByBranchCode(branchCode);

        if(restaurantConfByBranchCode.isEmpty()){

            //configuro qui i giorni di apertura per il branch appena configurato
            configureOpeningTime(branchCode);

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

            Optional<RestaurantConfiguration> restaurantConfiguration = restaurantConfigurationRepository.findByBranchCode(branchCode);

            WaApiConfigEntity waConfig = waApiConfigRepository.save(WaApiConfigEntity.builder()
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
                    .restaurantConfiguration(restaurantConfiguration.get())
                    .build());

            return BookingConfigurationDTO.builder()
                    .branchCode(branchCode)
                    .waApiConfigDTO(WaApiConfigDTO.fromEntity(waConfig))
                    .restaurantOpeningConfigurationDTO(RestaurantOpeningConfigurationDTO.builder()
                            .branchTimeRanges(BranchTimeRangeDTO.convertList(restaurantConfiguration.get().getBranchTimeRanges()))
                            .build())
                    .build();
        }else {
            // retrieve status and if is in qr code try to get the qr code
            log.info("Retrieve status of waapi instance and if is in qr code try to get a new qr code to configure branch with code {}", branchCode);
            MeResponse meResponse = retrieveClientInfo(restaurantConfByBranchCode.get().getWaApiConfig().getInstanceId());

            if("success".equalsIgnoreCase(meResponse.getStatus())
                    && "error".equalsIgnoreCase(meResponse.getMe().getStatus())){
                QrCodeResponse qrCodeResponse = retrieveQrCode(restaurantConfByBranchCode.get().getWaApiConfig().getInstanceId());
                restaurantConfByBranchCode.get().getWaApiConfig().setLastQrCode(qrCodeResponse.getQrCode().getData().getQrCode());
                restaurantConfByBranchCode.get().getWaApiConfig().setUpdateDate(new Date());
            }

            return BookingConfigurationDTO.builder()
                    .waApiConfigDTO(WaApiConfigDTO.fromEntity(restaurantConfByBranchCode.get().getWaApiConfig()))
                    .build();
        }
    }

    private void configureOpeningTime(String branchCode) {

        RestaurantConfiguration restaurantConfiguration = restaurantConfigurationRepository.save(
                RestaurantConfiguration.builder()
                        .restaurantConfId(0L)
                        .branchCode(branchCode)
                        .creationDate(new Date())
                        .build());

        List<BranchTimeRange> defaultTimeRanges = new ArrayList<>();


        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            BranchTimeRange timeRange = BranchTimeRange.builder()
                    .restaurantConfiguration(restaurantConfiguration)
                    .dayOfWeek(dayOfWeek)
                    .timeRanges(buildDefaultTimeRangeList())
                    .isOpen(false)
                    .build();

            defaultTimeRanges.add(timeRange);
        }

        branchTimeRangeRepository.saveAll(defaultTimeRanges);
        haveSomeTimeToSleep(500);
    }

    private List<TimeRange> buildDefaultTimeRangeList() {
        List<TimeRange> timeRanges = new ArrayList<>();

        timeRanges.add(TimeRange.builder()
                        .startTime(LocalTime.of(0, 0))
                        .endTime(LocalTime.of(0, 0))
                .build());

        return timeRanges;
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
    public BookingConfigurationDTO checkWaApiStatus(String branchCode) {

        log.info("Retrieve waapi configuration for branch with code {}" , branchCode);

        Optional<RestaurantConfiguration> restaurantConfiguration = restaurantConfigurationRepository.findByBranchCode(branchCode);

        if(restaurantConfiguration.isPresent()) {

            MeResponse meResponse = retrieveClientInfo(restaurantConfiguration.get().getWaApiConfig().getInstanceId());

            if("success".equalsIgnoreCase(meResponse.getStatus())
                    && "success".equalsIgnoreCase(meResponse.getMe().getStatus())){
                restaurantConfiguration.get().getWaApiConfig().setInstanceId(meResponse.getMe().getInstanceId());
                restaurantConfiguration.get().getWaApiConfig().setFormattedNumber(meResponse.getMe().getData().getFormattedNumber());
                restaurantConfiguration.get().getWaApiConfig().setDisplayName(meResponse.getMe().getData().getDisplayName());
                restaurantConfiguration.get().getWaApiConfig().setProfilePicUrl(meResponse.getMe().getData().getProfilePicUrl());
                restaurantConfiguration.get().getWaApiConfig().setInstanceStatus("OK");
                restaurantConfiguration.get().getWaApiConfig().setExplanation("");
                restaurantConfiguration.get().getWaApiConfig().setMessage("");
                restaurantConfiguration.get().getWaApiConfig().setLastQrCode("");
                restaurantConfiguration.get().getWaApiConfig().setUpdateDate(new Date());
            }


            return BookingConfigurationDTO.builder()
                    .branchCode(branchCode)
                    .waApiConfigDTO(WaApiConfigDTO.fromEntity(restaurantConfiguration.get().getWaApiConfig()))
                    .restaurantOpeningConfigurationDTO(RestaurantOpeningConfigurationDTO
                            .builder()
                            .branchTimeRanges(BranchTimeRangeDTO.convertList(restaurantConfiguration.get().getBranchTimeRanges()))
                            .build())
                    .build();
        }
        return null;
    }

    @Transactional
    public BookingConfigurationDTO reboot(String branchCode) {
        log.info("Reboot waapi configuration for branch with code {}" , branchCode);

        Optional<RestaurantConfiguration> restaurantConfiguration = restaurantConfigurationRepository.findByBranchCode(branchCode);

        if(restaurantConfiguration.isPresent()) {

            rebootInstance(restaurantConfiguration.get().getWaApiConfig().getInstanceId());
            haveSomeTimeToSleep(4000);

            MeResponse meResponse = retrieveClientInfo(restaurantConfiguration.get().getWaApiConfig().getInstanceId());

            if("success".equalsIgnoreCase(meResponse.getStatus())
                    && "success".equalsIgnoreCase(meResponse.getMe().getStatus())){
                restaurantConfiguration.get().getWaApiConfig().setInstanceId(meResponse.getMe().getInstanceId());
                restaurantConfiguration.get().getWaApiConfig().setFormattedNumber(meResponse.getMe().getData().getFormattedNumber());
                restaurantConfiguration.get().getWaApiConfig().setDisplayName(meResponse.getMe().getData().getDisplayName());
                restaurantConfiguration.get().getWaApiConfig().setProfilePicUrl(meResponse.getMe().getData().getProfilePicUrl());
                restaurantConfiguration.get().getWaApiConfig().setInstanceStatus("OK");
                restaurantConfiguration.get().getWaApiConfig().setExplanation("");
                restaurantConfiguration.get().getWaApiConfig().setMessage("");
                restaurantConfiguration.get().getWaApiConfig().setLastQrCode("");
                restaurantConfiguration.get().getWaApiConfig().setUpdateDate(new Date());
            }

            return BookingConfigurationDTO
                    .builder()
                    .waApiConfigDTO(WaApiConfigDTO.fromEntity(restaurantConfiguration.get().getWaApiConfig()))
                    .build();
        }
        return null;
    }


}
