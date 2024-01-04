package com.venticonsulting.waapi.service;

import com.venticonsulting.waapi.entity.BranchTimeRange;
import com.venticonsulting.waapi.entity.RestaurantConfiguration;
import com.venticonsulting.waapi.entity.WaApiConfigEntity;
import com.venticonsulting.waapi.entity.dto.RestaurantConfigurationDTO;
import com.venticonsulting.waapi.entity.dto.BranchTimeRangeDTO;
import com.venticonsulting.waapi.entity.dto.UpdateRestaurantConfigurationRequest;
import com.venticonsulting.waapi.entity.dto.WaApiConfigDTO;
import com.venticonsulting.waapi.entity.utils.WeekDayItalian;
import com.venticonsulting.waapi.entity.waapi.CreateUpdateResponse;
import com.venticonsulting.waapi.entity.waapi.MeResponse;
import com.venticonsulting.waapi.entity.waapi.QrCodeResponse;
import com.venticonsulting.waapi.repository.BranchTimeRangeRepository;
import com.venticonsulting.waapi.repository.RestaurantConfigurationRepository;
import com.venticonsulting.waapi.repository.WaApiConfigRepository;
import jakarta.el.MethodNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookingService {

    private final WaApiConfigRepository waApiConfigRepository;
    private final RestaurantConfigurationRepository restaurantConfigurationRepository;
    private final WaApiService waApiService;
    private final BranchTimeRangeRepository branchTimeRangeRepository;

    public BookingService(WaApiConfigRepository waApiConfigRepository,
                          RestaurantConfigurationRepository restaurantConfigurationRepository,
                          WaApiService waApiService,
                          BranchTimeRangeRepository branchTimeRangeRepository) {

        this.waApiConfigRepository = waApiConfigRepository;
        this.restaurantConfigurationRepository = restaurantConfigurationRepository;
        this.waApiService = waApiService;
        this.branchTimeRangeRepository = branchTimeRangeRepository;
    }

    @Transactional
    public RestaurantConfigurationDTO configureNumberForWhatsAppMessaging(String branchCode) {

        log.info("Create what's app configuration for branch with code {}" , branchCode);
        Optional<RestaurantConfiguration> restaurantConfByBranchCode
                = restaurantConfigurationRepository.findByBranchCode(branchCode);

        if(restaurantConfByBranchCode.isEmpty()){

            //configuro qui i giorni di apertura per il branch appena configurato
            configureOpeningTime(branchCode);

            log.info("There no configuration found for branch with code {}, let's create a brand new one.." , branchCode);
            CreateUpdateResponse createUpdateResponse = waApiService.createInstance();
            haveSomeTimeToSleep(1000);

            MeResponse meResponse;
            int maxIterations = 10;
            int currentIteration = 0;
            do {
                log.info("Retrieve a new created client info from waapi server untill it goes in 'qr' status. The qr code will be send to the client to configure the what'app app on a phone");

                meResponse = waApiService.retrieveClientInfo(createUpdateResponse.getInstance().getId());

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

            QrCodeResponse qrCodeResponse = waApiService.retrieveQrCode(createUpdateResponse.getInstance().getId());

            Optional<RestaurantConfiguration> restaurantConfiguration = restaurantConfigurationRepository
                    .findByBranchCode(branchCode);

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

            return RestaurantConfigurationDTO
                    .builder()
                    .bookingSlotInMinutes(0)
                    .allowOverbooking(restaurantConfiguration.get().isAllowOverbooking())
                    .guests(restaurantConfiguration.get().getGuests())
                    .branchCode(branchCode)
                    .waApiConfigDTO(WaApiConfigDTO.fromEntity(waConfig))
                    .branchTimeRanges(BranchTimeRangeDTO.convertList(restaurantConfiguration.get().getBranchTimeRanges()))
                    .build();
        }else {
            // retrieve status and if is in qr code try to get the qr code
            log.info("Retrieve status of waapi instance and if is in qr code try to get a new qr code to configure branch with code {}", branchCode);
            MeResponse meResponse = waApiService.retrieveClientInfo(restaurantConfByBranchCode.get().getWaApiConfig().getInstanceId());

            if("success".equalsIgnoreCase(meResponse.getStatus())
                    && "error".equalsIgnoreCase(meResponse.getMe().getStatus())){
                QrCodeResponse qrCodeResponse = waApiService.retrieveQrCode(restaurantConfByBranchCode.get().getWaApiConfig().getInstanceId());
                restaurantConfByBranchCode.get().getWaApiConfig().setLastQrCode(qrCodeResponse.getQrCode().getData().getQrCode());
                restaurantConfByBranchCode.get().getWaApiConfig().setUpdateDate(new Date());
            }

            return RestaurantConfigurationDTO.builder()
                    .guests(restaurantConfByBranchCode.get().getGuests())
                    .allowOverbooking(restaurantConfByBranchCode.get().isAllowOverbooking())
                    .waApiConfigDTO(WaApiConfigDTO.fromEntity(restaurantConfByBranchCode.get().getWaApiConfig()))
                    .bookingSlotInMinutes(0)
                    .build();
        }
    }

    private void configureOpeningTime(String branchCode) {

        RestaurantConfiguration restaurantConfiguration = restaurantConfigurationRepository.save(
                RestaurantConfiguration.builder()
                        .restaurantConfId(0L)
                        .guests(0)
                        .bookingSlotInMinutes(0)
                        .allowOverbooking(false)
                        .branchCode(branchCode)
                        .creationDate(new Date())
                        .build());

        List<BranchTimeRange> defaultTimeRanges = new ArrayList<>();


        for (WeekDayItalian dayOfWeek : WeekDayItalian.values()) {
            if(!WeekDayItalian.FESTIVO.equals(dayOfWeek)){
                BranchTimeRange timeRange = BranchTimeRange.builder()
                        .restaurantConfiguration(restaurantConfiguration)
                        .dayOfWeek(dayOfWeek)
                        .timeRanges(new ArrayList<>())
                        .build();
                defaultTimeRanges.add(timeRange);
            }
        }

        branchTimeRangeRepository.saveAll(defaultTimeRanges);
        haveSomeTimeToSleep(500);
    }

//    private List<TimeRange> buildDefaultTimeRangeList() {
//        List<TimeRange> timeRanges = new ArrayList<>();
//
//        timeRanges.add(TimeRange.builder()
//                .startTime(LocalTime.of(0, 0))
//                .endTime(LocalTime.of(0, 0))
//                .isOpen(false)
//                .build());
//
//        return timeRanges;
//    }

    private void haveSomeTimeToSleep(int sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            log.warn("Sleep time between creation instance on waapi server not working. Nothing bad actually, the process can be go on");
        }
    }

    @Transactional
    public RestaurantConfigurationDTO checkWaApiStatus(String branchCode) {

        log.info("Retrieve waapi configuration for branch with code {}" , branchCode);

        Optional<RestaurantConfiguration> restaurantConfiguration = restaurantConfigurationRepository.findByBranchCode(branchCode);

        if(restaurantConfiguration.isPresent()) {

            MeResponse meResponse = waApiService.retrieveClientInfo(restaurantConfiguration.get().getWaApiConfig().getInstanceId());

            if("success".equalsIgnoreCase(meResponse.getStatus())
                    && "success".equalsIgnoreCase(meResponse.getMe().getStatus())){
                restaurantConfiguration.get().getWaApiConfig().setInstanceId(meResponse.getMe().getInstanceId());
                restaurantConfiguration.get().getWaApiConfig().setFormattedNumber(meResponse.getMe().getData().getFormattedNumber());
                restaurantConfiguration.get().getWaApiConfig().setDisplayName(meResponse.getMe().getData().getDisplayName());
                restaurantConfiguration.get().getWaApiConfig().setProfilePicUrl(meResponse.getMe().getData().getProfilePicUrl());
                restaurantConfiguration.get().getWaApiConfig().setInstanceStatus(meResponse.getStatus());
                restaurantConfiguration.get().getWaApiConfig().setExplanation("");
                restaurantConfiguration.get().getWaApiConfig().setMessage("");
                restaurantConfiguration.get().getWaApiConfig().setLastQrCode("");
                restaurantConfiguration.get().getWaApiConfig().setUpdateDate(new Date());
            }else if("success".equalsIgnoreCase(meResponse.getStatus())
                    && "error".equalsIgnoreCase(meResponse.getMe().getStatus())){

                if (meResponse.getMe() != null
                        && meResponse.getMe().getMessage() != null) {
                    log.warn("Error - Seems that the instance is in error status while this exception happen {}", meResponse.getMe().getMessage());
                } else {
                    log.warn("Error - The ME response or its message is null.");
                }
                log.info("Perform a reboot action to restore instance with id {}", meResponse.getMe().getInstanceId());
                waApiService.rebootInstance(meResponse.getMe().getInstanceId());
                haveSomeTimeToSleep(2000);

                restaurantConfiguration.get().getWaApiConfig().setInstanceStatus(meResponse.getMe().getStatus());
                restaurantConfiguration.get().getWaApiConfig().setExplanation(meResponse.getMe().getExplanation());
                restaurantConfiguration.get().getWaApiConfig().setMessage(meResponse.getMe().getMessage());
                restaurantConfiguration.get().getWaApiConfig().setLastQrCode("");
                restaurantConfiguration.get().getWaApiConfig().setUpdateDate(new Date());

                //TODO: manage status qr or booting after reboot
            }

            return RestaurantConfigurationDTO.builder()
                    .guests(restaurantConfiguration.get().getGuests())
                    .allowOverbooking(restaurantConfiguration.get().isAllowOverbooking())
                    .branchCode(branchCode)
                    .bookingSlotInMinutes(0)
                    .waApiConfigDTO(WaApiConfigDTO.fromEntity(restaurantConfiguration.get().getWaApiConfig()))
                    .branchTimeRanges(BranchTimeRangeDTO.convertList(restaurantConfiguration.get().getBranchTimeRanges()))
                    .build();
        }
        return null;
    }

    @Transactional
    public RestaurantConfigurationDTO reboot(String branchCode) {
        log.info("Reboot waapi configuration for branch with code {}" , branchCode);

        Optional<RestaurantConfiguration> restaurantConfiguration = restaurantConfigurationRepository.findByBranchCode(branchCode);

        if(restaurantConfiguration.isPresent()) {

            waApiService.rebootInstance(restaurantConfiguration.get().getWaApiConfig().getInstanceId());
            haveSomeTimeToSleep(4000);

            MeResponse meResponse = waApiService.retrieveClientInfo(restaurantConfiguration.get().getWaApiConfig().getInstanceId());

            if("success".equalsIgnoreCase(meResponse.getStatus())
                    && "success".equalsIgnoreCase(meResponse.getMe().getStatus())){
                restaurantConfiguration.get().getWaApiConfig().setInstanceId(meResponse.getMe().getInstanceId());
                restaurantConfiguration.get().getWaApiConfig().setFormattedNumber(meResponse.getMe().getData().getFormattedNumber());
                restaurantConfiguration.get().getWaApiConfig().setDisplayName(meResponse.getMe().getData().getDisplayName());
                restaurantConfiguration.get().getWaApiConfig().setProfilePicUrl(meResponse.getMe().getData().getProfilePicUrl());
                restaurantConfiguration.get().getWaApiConfig().setInstanceStatus(meResponse.getMe().getStatus());
                restaurantConfiguration.get().getWaApiConfig().setExplanation("");
                restaurantConfiguration.get().getWaApiConfig().setMessage("");
                restaurantConfiguration.get().getWaApiConfig().setLastQrCode("");
                restaurantConfiguration.get().getWaApiConfig().setUpdateDate(new Date());
            }

            return RestaurantConfigurationDTO
                    .builder()
                    .bookingSlotInMinutes(0)
                    .guests(restaurantConfiguration.get().getGuests())
                    .allowOverbooking(restaurantConfiguration.get().isAllowOverbooking())
                    .waApiConfigDTO(WaApiConfigDTO.fromEntity(restaurantConfiguration.get().getWaApiConfig()))
                    .build();
        }
        return null;
    }


    @Transactional
    public RestaurantConfigurationDTO updateTimeRangeConfiguration(UpdateRestaurantConfigurationRequest updateRestaurantConfigurationRequest) {
        log.info("Updating restaurant configuration for branch with code {}, Booking Ids to update {}, Times Slot {} ",
                updateRestaurantConfigurationRequest.getBranchCode(),
                updateRestaurantConfigurationRequest.getListConfIds(),
                updateRestaurantConfigurationRequest.getTimeRanges().toString());

        Optional<List<BranchTimeRange>> byRestaurantConfIdIn
                = branchTimeRangeRepository.findByBranchTimeRangeId(updateRestaurantConfigurationRequest.getListConfIds());

        if(byRestaurantConfIdIn.isPresent()){
            for(BranchTimeRange branchTimeRange : byRestaurantConfIdIn.get()) {
                branchTimeRange.getTimeRanges().clear();
                branchTimeRange.getTimeRanges().addAll(updateRestaurantConfigurationRequest.getTimeRanges());
            }
        }else{
            throw new MethodNotFoundException("Method not implemented yet");
        }

        Optional<RestaurantConfiguration> restaurantConfiguration = restaurantConfigurationRepository.findByBranchCode(updateRestaurantConfigurationRequest.getBranchCode());

        return restaurantConfiguration.map(configuration -> RestaurantConfigurationDTO.builder()
                .guests(configuration.getGuests())
                .allowOverbooking(configuration.isAllowOverbooking())
                .branchCode(restaurantConfiguration.get().getBranchCode())
                .bookingSlotInMinutes(0)
                .waApiConfigDTO(WaApiConfigDTO.fromEntity(configuration.getWaApiConfig()))
                .branchTimeRanges(BranchTimeRangeDTO.convertList(configuration.getBranchTimeRanges()))
                .build()).orElse(null);

    }


}
