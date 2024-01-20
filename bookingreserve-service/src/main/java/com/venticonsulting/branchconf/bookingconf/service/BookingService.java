package com.venticonsulting.branchconf.bookingconf.service;

import com.venticonsulting.branchconf.bookingconf.entity.booking.Booking;
import com.venticonsulting.branchconf.bookingconf.entity.configuration.*;
import com.venticonsulting.branchconf.bookingconf.entity.dto.*;
import com.venticonsulting.branchconf.bookingconf.entity.utils.Utils;
import com.venticonsulting.branchconf.bookingconf.entity.utils.WeekDayItalian;
import com.venticonsulting.branchconf.bookingconf.repository.*;
import com.venticonsulting.branchconf.waapiconf.entity.WaApiConfigEntity;
import com.venticonsulting.branchconf.waapiconf.dto.WaApiConfigDTO;
import com.venticonsulting.branchconf.waapiconf.dto.CreateUpdateResponse;
import com.venticonsulting.branchconf.waapiconf.dto.MeResponse;
import com.venticonsulting.branchconf.waapiconf.dto.QrCodeResponse;
import com.venticonsulting.branchconf.waapiconf.repository.WaApiConfigRepository;
import com.venticonsulting.branchconf.waapiconf.service.WaApiService;
import com.venticonsulting.exception.BranchNotFoundException;
import jakarta.el.MethodNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookingService {

    private final WaApiConfigRepository waApiConfigRepository;
    private final BranchConfigurationRepository branchConfigurationRepository;
    private final BookingFormRespository bookingFormRespository;
    private final WaApiService waApiService;
    private final BranchTimeRangeRepository branchTimeRangeRepository;
    private final CustomerRepository customerRepository;

    private final BookingRepository bookingRepository;



    public BookingService(WaApiConfigRepository waApiConfigRepository,
                          BranchConfigurationRepository branchConfigurationRepository,
                          BookingFormRespository bookingFormRespository, WaApiService waApiService,
                          BranchTimeRangeRepository branchTimeRangeRepository, CustomerRepository customerRepository, BookingRepository bookingRepository) {

        this.waApiConfigRepository = waApiConfigRepository;
        this.branchConfigurationRepository = branchConfigurationRepository;
        this.bookingFormRespository = bookingFormRespository;
        this.waApiService = waApiService;
        this.branchTimeRangeRepository = branchTimeRangeRepository;
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public BranchConfigurationDTO configureNumberForWhatsAppMessaging(String branchCode) {

        log.info("Create what's app configuration for branch with code {}" , branchCode);
        Optional<BranchConfiguration> branchConfByBranchCode
                = branchConfigurationRepository.findByBranchCode(branchCode);

        if(branchConfByBranchCode.isEmpty()){

            log.info("There no configuration found for branch with code {}, let's create a brand new one.." , branchCode);
            //configuro qui i giorni di apertura per il branch appena configurato

            createBranchConfiguration(branchCode);

            CreateUpdateResponse createUpdateResponse = waApiService.createInstance();
            haveSomeTimeToSleep(2000);

            MeResponse meResponse;
            int maxIterations = 10;
            int currentIteration = 0;
            do {
                log.info("Retrieve a new created client info from waapi server untill it goes in 'qr' status. The qr code will be send to the client to configure the what'app app on a phone");

                meResponse = waApiService.retrieveClientInfo(createUpdateResponse.getInstance().getId());

                if (meResponse != null
                        && !"success".equals(meResponse.getStatus())) {
                    haveSomeTimeToSleep(2000);
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

            Optional<BranchConfiguration> branchConfiguration = branchConfigurationRepository
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
                    .branchConfiguration(branchConfiguration.get())
                    .build());


            return BranchConfigurationDTO
                    .builder()
                    .isReservationConfirmedManually(branchConfiguration.get().isReservationConfirmedManually())
                    .bookingSlotInMinutes(0)
                    .guestReceivingAuthConfirm(branchConfiguration.get().getGuestReceivingAuthConfirm())
                    .branchConfId(branchConfiguration.get().getBranchConfId())
                    .minBeforeSendConfirmMessage(branchConfiguration.get().getMinBeforeSendConfirmMessage())
                    .guests(branchConfiguration.get().getGuests())
                    .branchCode(branchCode)
                    .maxTableNumber(branchConfiguration.get().getMaxTableNumber())
                    .waApiConfigDTO(WaApiConfigDTO.fromEntity(waConfig))
                    .tags(branchConfiguration.get().getTags())

//                    .branchTimeRanges(BranchTimeRangeDTO.convertList(branchConfiguration.get().getBranchTimeRanges()))
                    .build();
        }else {
            // retrieve status and if is in qr code try to get the qr code
            log.info("Retrieve status of waapi instance and if is in qr code try to get a new qr code to configure branch with code {}", branchCode);
            MeResponse meResponse = waApiService.retrieveClientInfo(branchConfByBranchCode.get().getWaApiConfig().getInstanceId());

            if("success".equalsIgnoreCase(meResponse.getStatus())
                    && "error".equalsIgnoreCase(meResponse.getMe().getStatus())){
                QrCodeResponse qrCodeResponse = waApiService.retrieveQrCode(branchConfByBranchCode.get().getWaApiConfig().getInstanceId());
                branchConfByBranchCode.get().getWaApiConfig().setLastQrCode(qrCodeResponse.getQrCode().getData().getQrCode());
                branchConfByBranchCode.get().getWaApiConfig().setUpdateDate(new Date());
            }

            return BranchConfigurationDTO.fromEntity(branchConfByBranchCode.get());
        }
    }

    private List<FormTag> buildDefaultTagsList() {
        List<FormTag> tags = new ArrayList<>();
        tags.add(FormTag.builder().id(0L).title("Cena").build());
        tags.add(FormTag.builder().id(0L).title("Pranzo").build());

        return tags;
    }

    private void createBranchConfiguration(String branchCode) {

        BranchConfiguration branchConfiguration = branchConfigurationRepository.save(
                BranchConfiguration.builder()
                        .branchConfId(0L)
                        .isReservationConfirmedManually(false)
                        .minBeforeSendConfirmMessage(0)
                        .guestReceivingAuthConfirm(0)
                        .guests(0)
                        .bookingSlotInMinutes(0)
                        .bookingSlotInMinutes(0)
                        .maxTableNumber(0)
                        .branchCode(branchCode)
                        .tags(buildDefaultTagsList())
                        .creationDate(new Date())
                        .build());

        BookingForm bookingForm = bookingFormRespository.save(
                BookingForm.builder()
                        .formId(0L)
                        .isDefaultForm(true)
                        .formType(BookingForm.FormType.BOOKING_FORM)
                        .redirectPage("")
                        .formName("Form Default")
                        .branchConfiguration(branchConfiguration)
                        .creationDate(new Date())
                        .build());

        List<BranchTimeRange> defaultTimeRanges = new ArrayList<>();

        for (WeekDayItalian dayOfWeek : WeekDayItalian.values()) {
            if(!WeekDayItalian.FESTIVO.equals(dayOfWeek)){
                BranchTimeRange timeRange = BranchTimeRange.builder()
                        .bookingForm(bookingForm)
                        .dayOfWeek(dayOfWeek)
                        .isClosed(true)
                        .timeRanges(new ArrayList<>())
                        .build();
                defaultTimeRanges.add(timeRange);
            }
        }

        branchTimeRangeRepository.saveAll(defaultTimeRanges);
        haveSomeTimeToSleep(500);
    }


    private void haveSomeTimeToSleep(int sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            log.warn("Sleep time between creation instance on waapi server not working. Nothing bad actually, the process can be go on");
        }
    }

    @Transactional
    public BranchConfigurationDTO checkWaApiStatus(String branchCode) {

        log.info("Retrieve waapi configuration for branch with code {}" , branchCode);

        Optional<BranchConfiguration> branchConfiguration = branchConfigurationRepository.findByBranchCode(branchCode);

        if(branchConfiguration.isPresent()) {

            if(Utils.isThisDateGraterThanNOWOfGivingMinuteValue(branchConfiguration.get().getLastWaApiConfCheck(), 5)){
                log.info("Passing more than 5 minutes from last update. Contact WaApi again and refresh configuration");
                MeResponse meResponse = waApiService.retrieveClientInfo(branchConfiguration.get().getWaApiConfig().getInstanceId());

                if("success".equalsIgnoreCase(meResponse.getStatus())
                        && "success".equalsIgnoreCase(meResponse.getMe().getStatus())){
                    branchConfiguration.get().getWaApiConfig().setInstanceId(meResponse.getMe().getInstanceId());
                    branchConfiguration.get().getWaApiConfig().setFormattedNumber(meResponse.getMe().getData().getFormattedNumber());
                    branchConfiguration.get().getWaApiConfig().setDisplayName(meResponse.getMe().getData().getDisplayName());
                    branchConfiguration.get().getWaApiConfig().setProfilePicUrl(meResponse.getMe().getData().getProfilePicUrl());
                    branchConfiguration.get().getWaApiConfig().setInstanceStatus(meResponse.getStatus());
                    branchConfiguration.get().getWaApiConfig().setExplanation("");
                    branchConfiguration.get().getWaApiConfig().setMessage("");
                    branchConfiguration.get().getWaApiConfig().setLastQrCode("");
                    branchConfiguration.get().setLastWaApiConfCheck(new Date());
                    branchConfiguration.get().getWaApiConfig().setUpdateDate(new Date());

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
                    haveSomeTimeToSleep(5000);

                    branchConfiguration.get().getWaApiConfig().setInstanceStatus(meResponse.getMe().getStatus());
                    branchConfiguration.get().getWaApiConfig().setExplanation(meResponse.getMe().getExplanation());
                    branchConfiguration.get().getWaApiConfig().setMessage(meResponse.getMe().getMessage());
                    branchConfiguration.get().getWaApiConfig().setLastQrCode("");
                    branchConfiguration.get().getWaApiConfig().setUpdateDate(new Date());

                    //TODO: manage status qr or booting after reboot
                }
            }else{
                log.info("Using old configuration");
            }

            return BranchConfigurationDTO.fromEntity(branchConfiguration.get());
        }
        return null;
    }

    @Transactional
    public BranchConfigurationDTO reboot(String branchCode) {
        log.info("Reboot waapi configuration for branch with code {}" , branchCode);

        Optional<BranchConfiguration> branchConfiguration = branchConfigurationRepository.findByBranchCode(branchCode);

        if(branchConfiguration.isPresent()) {

            waApiService.rebootInstance(branchConfiguration.get().getWaApiConfig().getInstanceId());
            haveSomeTimeToSleep(4000);

            MeResponse meResponse = waApiService.retrieveClientInfo(branchConfiguration.get().getWaApiConfig().getInstanceId());

            if("success".equalsIgnoreCase(meResponse.getStatus())
                    && "success".equalsIgnoreCase(meResponse.getMe().getStatus())){
                branchConfiguration.get().getWaApiConfig().setInstanceId(meResponse.getMe().getInstanceId());
                branchConfiguration.get().getWaApiConfig().setFormattedNumber(meResponse.getMe().getData().getFormattedNumber());
                branchConfiguration.get().getWaApiConfig().setDisplayName(meResponse.getMe().getData().getDisplayName());
                branchConfiguration.get().getWaApiConfig().setProfilePicUrl(meResponse.getMe().getData().getProfilePicUrl());
                branchConfiguration.get().getWaApiConfig().setInstanceStatus(meResponse.getMe().getStatus());
                branchConfiguration.get().getWaApiConfig().setExplanation("");
                branchConfiguration.get().getWaApiConfig().setMessage("");
                branchConfiguration.get().getWaApiConfig().setLastQrCode("");
                branchConfiguration.get().getWaApiConfig().setUpdateDate(new Date());
            }

            return BranchConfigurationDTO.fromEntity(branchConfiguration.get());
        }
        return null;
    }


    @Transactional
    public BranchConfigurationDTO updateTimeRangeConfiguration(UpdateBranchConfigurationRequest updateBranchConfigurationRequest) {
        log.info("Updating branch configuration for branch with code {}, Booking Ids to update {}, Times Slot {} ",
                updateBranchConfigurationRequest.getBranchCode(),
                updateBranchConfigurationRequest.getListConfIds(),
                updateBranchConfigurationRequest.getTimeRanges().toString());

        Optional<List<BranchTimeRange>> byBranchTimeRangeId
                = branchTimeRangeRepository.findByBranchTimeRangeId(updateBranchConfigurationRequest.getListConfIds());

        if(byBranchTimeRangeId.isPresent()){

            for(BranchTimeRange branchTimeRange : byBranchTimeRangeId.get()) {
                branchTimeRange.getTimeRanges().clear();
                branchTimeRange.getTimeRanges().addAll(TimeRangeUpdateRequest.convertTimeRange(updateBranchConfigurationRequest.getTimeRanges()));
            }
        }else{
            throw new MethodNotFoundException("Method not implemented yet");
        }

        Optional<BranchConfiguration> branchConfiguration = branchConfigurationRepository.findByBranchCode(updateBranchConfigurationRequest.getBranchCode());

        return BranchConfigurationDTO.fromEntity(branchConfiguration.get());

    }


    @Transactional
    public BranchConfigurationDTO deleteTimeRange(long timeRangeId) {
        //TODO to implement
        return null;
    }

    @Transactional
    public BranchConfigurationDTO updateConfiguration(BranchOpeningEditConfigurationRequest branchOpeningEditConfigurationRequest) {

        log.info("Updating reservation configuration for branch with code {} ", branchOpeningEditConfigurationRequest.getBranchCode());
        Optional<BranchConfiguration> byBranchCode = branchConfigurationRepository.findByBranchCode(branchOpeningEditConfigurationRequest.getBranchCode());

        if(byBranchCode.isPresent()){

            byBranchCode.get().setGuests(branchOpeningEditConfigurationRequest.getGuests());
            byBranchCode.get().setReservationConfirmedManually(branchOpeningEditConfigurationRequest.isReservationConfirmedManually());
            byBranchCode.get().setBookingSlotInMinutes(branchOpeningEditConfigurationRequest.getBookingSlotInMinutes());
            byBranchCode.get().setGuestReceivingAuthConfirm(branchOpeningEditConfigurationRequest.getGuestReceivingAuthConfirm());
            byBranchCode.get().setMinBeforeSendConfirmMessage(branchOpeningEditConfigurationRequest.getMinBeforeSendConfirmMessage());
            byBranchCode.get().setMaxTableNumber(branchOpeningEditConfigurationRequest.getMaxTableNumber());

            return BranchConfigurationDTO.fromEntity(byBranchCode.get());
        }else{
            return null;
        }
    }

    @Transactional
    public FormTag createTag(String tagName, String branchCode) {

        log.info("Create tag with name {} for branch with code {}", tagName, branchCode);
        Optional<BranchConfiguration> byBranchCode = branchConfigurationRepository.findByBranchCode(branchCode);
        byBranchCode.ifPresent(branchConfiguration -> branchConfiguration.getTags().add(FormTag.builder().title(tagName).build()));

        return byBranchCode.get().getTags().stream()
                .filter(formTag -> formTag.getTitle().equals(tagName))
                .findFirst().get();
    }

    @Transactional
    public void deleteTag(String tagName, String branchCode) {

        log.info("Delete tag with name {} for branch with code {}", tagName, branchCode);

        branchConfigurationRepository
                .findByBranchCode(branchCode)
                    .flatMap(branchConfiguration -> Optional.ofNullable(branchConfiguration.getTags())).ifPresent(tags -> tags.removeIf(formTag -> formTag.getTitle().equals(tagName)));
    }

    public CustomerFormData retrieveFormData(String branchCode,
                                             String formCode) {

        log.info("Retrieve form with default configuration for branch with code{}, " +
                "form code {}", branchCode, formCode);

        Optional<BranchConfiguration> branchConf = branchConfigurationRepository.findByBranchCode(branchCode);

        if(branchConf.isPresent()){
            Optional<BookingForm> form = branchConf.get().getBookingForms().stream()
                    .filter(bookingForm -> formCode.equals(bookingForm.getFormCode()))
                    .findFirst();

            if(form.isPresent()){

                List<Booking> bookingList = bookingRepository.findByBranchCode(branchCode);

                log.info("Prepare data for the booking form with code {}", formCode);

                form.get().getBranchTimeRanges();

                return CustomerFormData.builder()
                        .bookingSlotInMinutes(branchConf.get().getBookingSlotInMinutes())



                        .branchTimeRanges(form.get().getBranchTimeRanges())
                        .build();
            }else{

                log.error("No form found with code " + formCode);
            }
        }else{
            throw new BranchNotFoundException("Branch not found for code " + branchCode);
        }
    }
}
