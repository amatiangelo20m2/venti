package com.venticonsulting.branchconf.bookingconf.service;

import com.venticonsulting.branchconf.bookingconf.entity.booking.Booking;
import com.venticonsulting.branchconf.bookingconf.entity.booking.Customer;
import com.venticonsulting.branchconf.bookingconf.entity.configuration.*;
import com.venticonsulting.branchconf.bookingconf.entity.dto.*;
import com.venticonsulting.branchconf.bookingconf.entity.utils.Utils;
import com.venticonsulting.branchconf.bookingconf.entity.utils.WeekDayItalian;
import com.venticonsulting.branchconf.bookingconf.repository.*;
import com.venticonsulting.branchconf.waapiconf.dto.CreateUpdateResponse;
import com.venticonsulting.branchconf.waapiconf.dto.MeResponse;
import com.venticonsulting.branchconf.waapiconf.dto.QrCodeResponse;
import com.venticonsulting.branchconf.waapiconf.service.WaApiService;
import com.venticonsulting.exception.customException.BranchNotFoundException;
import com.venticonsulting.exception.customException.FormNotFoundException;
import jakarta.el.MethodNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
public class BookingService {

    private final BranchConfigurationRepository branchConfigurationRepository;
    private final BookingFormRepository bookingFormRepository;
    private final WaApiService waApiService;
    private final BranchTimeRangeRepository branchTimeRangeRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;

    public BookingService(BranchConfigurationRepository branchConfigurationRepository,
                          BookingFormRepository bookingFormRepository,
                          WaApiService waApiService,
                          BranchTimeRangeRepository branchTimeRangeRepository,
                          CustomerRepository customerRepository,
                          BookingRepository bookingRepository) {

        this.branchConfigurationRepository = branchConfigurationRepository;
        this.bookingFormRepository = bookingFormRepository;
        this.waApiService = waApiService;
        this.branchTimeRangeRepository = branchTimeRangeRepository;
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public BranchConfigurationDTO configureNumberForWhatsAppMessaging(String branchCode) {

        log.info("Create what's app configuration for branch with code {}" , branchCode);
        Optional<BranchConfiguration> branchConfByBranchCode
                = branchConfigurationRepository.findByBranchCode(branchCode);

        if(branchConfByBranchCode.isEmpty()){

            log.info("There is no configuration found for branch with code {}, let's create a brand new one.." , branchCode);
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

            Optional<BranchConfiguration> branchConfiguration = branchConfigurationRepository.findByBranchCode(branchCode);

            branchConfiguration.get()
                    .setOwner(createUpdateResponse.getInstance().getOwner())
                    .setFormattedNumber("")
                    .setInstanceStatus(meResponse.getMe().getInstanceStatus())
                    .setInstanceCreationDate(new Date())
                    .setInstanceId(createUpdateResponse.getInstance().getId())
                    .setLastQrCode(qrCodeResponse.getQrCode().getData().getQrCode())
                    .setProfilePicUrl("")
                    .setMessage(meResponse.getMe().getMessage())
                    .setExplanation(meResponse.getMe().getExplanation());


            return BranchConfigurationDTO.fromEntity(branchConfiguration.get());
        }else {
            // retrieve status and if is in qr code try to get the qr code
            log.info("Retrieve status of waapi instance and if is in qr code try to get a new qr code to configure branch with code {}", branchCode);
            MeResponse meResponse = waApiService.retrieveClientInfo(branchConfByBranchCode.get().getInstanceId());

            if("success".equalsIgnoreCase(meResponse.getStatus())
                    && "error".equalsIgnoreCase(meResponse.getMe().getStatus())){
                QrCodeResponse qrCodeResponse = waApiService.retrieveQrCode(branchConfByBranchCode.get().getInstanceId());
                branchConfByBranchCode.get().setLastQrCode(qrCodeResponse.getQrCode().getData().getQrCode());
                branchConfByBranchCode.get().setLastWaApiConfCheck(new Date());
            }

            return BranchConfigurationDTO.fromEntity(branchConfByBranchCode.get());
        }
    }

    private List<FormTag> buildDefaultTagsList() {
        List<FormTag> tags = new ArrayList<>();
        FormTag formTag = new FormTag();
        formTag.setTitle("Cena");
        formTag.setActive(false);
        FormTag formTag1 = new FormTag();
        formTag1.setTitle("Pranzo");
        formTag1.setActive(false);
        tags.add(formTag);
        tags.add(formTag1);

        return tags;
    }
    @Transactional
    public void createBranchConfiguration(String branchCode) {
        BranchConfiguration branchConfiguration = BranchConfiguration.builder()
                .isReservationConfirmedManually(false)
                .minBeforeSendConfirmMessage(0)
                .guestReceivingAuthConfirm(0)
                .guests(0)
                .displayName("")
                .contactId("")
                .bookingSlotInMinutes(0)
                .maxTableNumber(0)
                .branchCode(branchCode)
                .lastWaApiConfCheck(new Date())
                .tags(buildDefaultTagsList()) // Uncomment if buildDefaultTagsList() is available
                .branchConfCreationDate(new Date())
                .bookingForms(new ArrayList<>())
                .build();

        BookingForm bookingForm = BookingForm.builder()
                .isDefaultForm(true)
                .formType(BookingForm.FormType.BOOKING_FORM)
                .redirectPage("")
                .formName("Form Default")
                .address("")
                .formLogo("")
                .description("Form prenotazione di default")
                .branchConfiguration(branchConfiguration)
                .creationDate(new Date())
                .branchTimeRanges(new ArrayList<>())
                .build();

        for (WeekDayItalian dayOfWeek : WeekDayItalian.values()) {
            if (!WeekDayItalian.FESTIVO.equals(dayOfWeek)) {
                BranchTimeRange timeRange = BranchTimeRange.builder()
                        .bookingForm(bookingForm)
                        .dayOfWeek(dayOfWeek)
                        .isClosed(true)
                        .timeRanges(new ArrayList<>())
                        .build();
                bookingForm.getBranchTimeRanges().add(timeRange);
            }
        }

        branchConfiguration.getBookingForms().add(bookingForm);

        branchConfigurationRepository.save(branchConfiguration);

        haveSomeTimeToSleep(500);

    }

    @Transactional
    public BranchConfigurationDTO checkWaApiStatus(String branchCode) {

        log.info("Retrieve waapi configuration for branch with code {}" , branchCode);

        Optional<BranchConfiguration> branchConfiguration = branchConfigurationRepository.findByBranchCode(branchCode);

        if(branchConfiguration.isPresent()) {

            if(!Objects.equals(branchConfiguration.get().getInstanceStatus(), "success")
                    ||  Utils.isThisDateGraterThanNOWOfGivingMinuteValue(branchConfiguration.get().getLastWaApiConfCheck(), 3)){
                log.info("The configuration is not in 'success' instance status or are passed more than 5 minutes from last update or intance status is not in success. Contact WaApi again and refresh configuration");
                MeResponse meResponse = waApiService.retrieveClientInfo(branchConfiguration.get().getInstanceId());

                if("success".equalsIgnoreCase(meResponse.getStatus())
                        && "success".equalsIgnoreCase(meResponse.getMe().getStatus())){

                    branchConfiguration.get().setInstanceId(meResponse.getMe().getInstanceId());
                    branchConfiguration.get().setFormattedNumber(meResponse.getMe().getData().getFormattedNumber());
                    branchConfiguration.get().setDisplayName(meResponse.getMe().getData().getDisplayName());
                    branchConfiguration.get().setContactId(meResponse.getMe().getData().getContactId());
                    branchConfiguration.get().setFormattedNumber(meResponse.getMe().getData().getFormattedNumber());

                    branchConfiguration.get().setProfilePicUrl(meResponse.getMe().getData().getProfilePicUrl());
                    branchConfiguration.get().setInstanceStatus(meResponse.getStatus());
                    branchConfiguration.get().setExplanation("");
                    branchConfiguration.get().setMessage("");
//                    branchConfiguration.get().setLastQrCode("");

                    branchConfiguration.get().setLastWaApiConfCheck(new Date());
                    branchConfiguration.get().setInstanceUpdateDate(new Date());

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

                    branchConfiguration.get().setInstanceStatus(meResponse.getMe().getStatus());
                    branchConfiguration.get().setExplanation(meResponse.getMe().getExplanation());
                    branchConfiguration.get().setMessage(meResponse.getMe().getMessage());
                    branchConfiguration.get().setLastQrCode("");
                    branchConfiguration.get().setInstanceUpdateDate(new Date());

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

            waApiService.rebootInstance(branchConfiguration.get().getInstanceId());
            haveSomeTimeToSleep(4000);

            MeResponse meResponse = waApiService.retrieveClientInfo(branchConfiguration.get().getInstanceId());

            if("success".equalsIgnoreCase(meResponse.getStatus())
                    && "success".equalsIgnoreCase(meResponse.getMe().getStatus())){
                branchConfiguration.get().setInstanceId(meResponse.getMe().getInstanceId());
                branchConfiguration.get().setFormattedNumber(meResponse.getMe().getData().getFormattedNumber());
                branchConfiguration.get().setDisplayName(meResponse.getMe().getData().getDisplayName());
                branchConfiguration.get().setProfilePicUrl(meResponse.getMe().getData().getProfilePicUrl());
                branchConfiguration.get().setInstanceStatus(meResponse.getMe().getStatus());
                branchConfiguration.get().setExplanation("");
                branchConfiguration.get().setMessage("");
                branchConfiguration.get().setLastQrCode("");
                branchConfiguration.get().setInstanceUpdateDate(new Date());
            }

            return BranchConfigurationDTO.fromEntity(branchConfiguration.get());
        }
        return null;
    }


    @Transactional
    public BranchConfigurationDTO updateTimeRangeConfiguration(UpdateBranchTimeRanges updateBranchTimeRanges) {
        log.info("Updating branch configuration for branch with code {}, Booking Ids to update {}, Times Slot {} ",
                updateBranchTimeRanges.getBranchCode(),
                updateBranchTimeRanges.getListConfIds(),
                updateBranchTimeRanges.getTimeRanges().toString());

        Optional<List<BranchTimeRange>> byBranchTimeRangeId
                = branchTimeRangeRepository.findByBranchTimeRangeId(updateBranchTimeRanges.getListConfIds());

        if(byBranchTimeRangeId.isPresent()){

            for(BranchTimeRange branchTimeRange : byBranchTimeRangeId.get()) {
                branchTimeRange.getTimeRanges().clear();
                branchTimeRange.getTimeRanges().addAll(TimeRangeUpdateRequest.convertTimeRange(updateBranchTimeRanges.getTimeRanges()));
            }
        }else{
            throw new MethodNotFoundException("Method not implemented yet");
        }

        Optional<BranchConfiguration> branchConfiguration = branchConfigurationRepository.findByBranchCode(updateBranchTimeRanges.getBranchCode());

        return BranchConfigurationDTO.fromEntity(branchConfiguration.get());

    }


    @Transactional
    public BranchConfigurationDTO deleteTimeRange(long timeRangeId) {
        //TODO to implement
        return null;
    }

    @Transactional
    public BranchConfigurationDTO updateConfiguration(BranchGeneralConfigurationEditRequest branchGeneralConfigurationEditRequest) {

        log.info("Updating reservation configuration for branch with code {} ", branchGeneralConfigurationEditRequest.getBranchCode());
        Optional<BranchConfiguration> byBranchCode = branchConfigurationRepository.findByBranchCode(branchGeneralConfigurationEditRequest.getBranchCode());

        if(byBranchCode.isPresent()){

            byBranchCode.get().setGuests(branchGeneralConfigurationEditRequest.getGuests());
            byBranchCode.get().setReservationConfirmedManually(branchGeneralConfigurationEditRequest.isReservationConfirmedManually());
            byBranchCode.get().setBookingSlotInMinutes(branchGeneralConfigurationEditRequest.getBookingSlotInMinutes());
            byBranchCode.get().setGuestReceivingAuthConfirm(branchGeneralConfigurationEditRequest.getGuestReceivingAuthConfirm());
            byBranchCode.get().setMinBeforeSendConfirmMessage(branchGeneralConfigurationEditRequest.getMinBeforeSendConfirmMessage());
            byBranchCode.get().setMaxTableNumber(branchGeneralConfigurationEditRequest.getMaxTableNumber());

            return BranchConfigurationDTO.fromEntity(byBranchCode.get());
        }else{
            return null;
        }
    }

//    @Transactional
//    public FormTag createTag(String tagName, String branchCode) {
//
//        log.info("Create tag with name {} for branch with code {}", tagName, branchCode);
//        FormTag formTagCreate = new FormTag();
//        formTagCreate.setTitle(tagName);
//        Optional<BranchConfiguration> byBranchCode = branchConfigurationRepository.findByBranchCode(branchCode);
//        byBranchCode.ifPresent(branchConfiguration
//                ->
//                branchConfiguration.getTags().add(formTagCreate));
//
//        return byBranchCode.get().getTags().stream()
//                .filter(formTag -> formTag.getTitle().equals(tagName))
//                .findFirst().get();
//    }
//
//    @Transactional
//    public void deleteTag(String tagName, String branchCode) {
//
//        log.info("Delete tag with name {} for branch with code {}", tagName, branchCode);
//
//        branchConfigurationRepository
//                .findByBranchCode(branchCode)
//                .flatMap(branchConfiguration -> Optional.ofNullable(branchConfiguration.getTags())).ifPresent(tags -> tags.removeIf(formTag -> formTag.getTitle().equals(tagName)));
//    }

    public CustomerFormData retrieveFormData(String branchCode, String formCode) {
        log.info("Retrieve form with default configuration for branch with code {}, form code {}", branchCode, formCode);

        Optional<BranchConfiguration> branchConf = branchConfigurationRepository.findByBranchCode(branchCode);

        if (branchConf.isPresent()) {
            Optional<BookingForm> form = branchConf.get().getBookingForms().stream()
                    .filter(bookingForm -> formCode.equals(bookingForm.getFormCode()))
                    .findFirst();

            if (form.isPresent()) {

                List<Object[]> guestsByDateAndTime = bookingRepository.countGuestsByDateAndTime(branchCode, LocalDate.now());

                Map<LocalDate, Map<LocalTime, Integer>> localDateMapMap = new HashMap<>();

                for (Object[] row : guestsByDateAndTime) {

                    LocalDate date = (LocalDate) row[0];
                    LocalTime time = (LocalTime) row[1];

                    int totalGuests = ((Number) row[2]).intValue();

                    log.info("Date: " + date + ",WeekDay " + date.getDayOfWeek() +
                            " ,Time: " + time + ", Total Guests: " + totalGuests);

                    localDateMapMap.putIfAbsent(date, new HashMap<>());
                    localDateMapMap.get(date).put(time, totalGuests);

                }

                List<DateTimeRangeAvailableGuests> dateTimeRangeAvailableGuests = new ArrayList<>();

                for (BranchTimeRange branchTimeRange : form.get().getBranchTimeRanges()) {
                    if (!branchTimeRange.isClosed()) {
                        for (Map.Entry<LocalDate, Map<LocalTime, Integer>> dateEntry : localDateMapMap.entrySet()) {
                            if(WeekDayItalian.fromEngDayOfWeek(dateEntry.getKey().getDayOfWeek()).equals(branchTimeRange.getDayOfWeek())){

                                for(TimeRange timeRange : branchTimeRange.getTimeRanges()){
                                    dateTimeRangeAvailableGuests.add(
                                            DateTimeRangeAvailableGuests.builder()
                                                    .date(dateEntry.getKey())
                                                    .timeRange(timeRange)
                                                    .guestsAvailable(calculateGuestsInTimeRange(timeRange, dateEntry.getValue(), branchConf.get().getGuests()))
                                                    .build()
                                    );
                                }
                            }
                        }
                    }
                }



                return CustomerFormData.builder()
                        .branchCode(branchCode)
                        .formCode(formCode)
                        .bookingSlotInMinutes(branchConf.get().getBookingSlotInMinutes())
                        .formLogo(form.get().getFormLogo())
                        .address(form.get().getAddress())
                        .maxTableNumber(branchConf.get().getMaxTableNumber())
                        .dateTimeRangeAvailableGuests(dateTimeRangeAvailableGuests)
                        .build();
            } else {
                log.error("Form not found for code {}", formCode);
                throw new FormNotFoundException("Form not found for code " + formCode);
            }
        } else {
            log.error("Branch not found for code {}", branchCode);
            throw new BranchNotFoundException("Branch not found for code " + branchCode);
        }
    }

    private int calculateGuestsInTimeRange(TimeRange timeRange, Map<LocalTime, Integer> timeGuestsMap, int maxGuests) {
        int totalGuests = 0;

        for (Map.Entry<LocalTime, Integer> entry : timeGuestsMap.entrySet()) {
            LocalTime time = entry.getKey();
            int guests = entry.getValue();

            // Check if the time falls within the specified time range
            if (isTimeInRange(time, timeRange)) {
                totalGuests += guests;
            }
        }

        return maxGuests - totalGuests;
    }

    private boolean isTimeInRange(LocalTime time, TimeRange timeRange) {
        return !time.isBefore(timeRange.getStartTime()) && !time.isAfter(timeRange.getEndTime());
    }

    public static void haveSomeTimeToSleep(int sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            log.warn("Sleep time between creation instance on waapi server not working. Nothing bad actually, the process can be go on");
        }
    }

    public void createBooking(CreateBookingRequest createBookingRequest) {
        log.info("Create booking with the following data {}", createBookingRequest);
        Optional<Customer> byPhoneOrEmail = customerRepository
                .findByPhoneOrEmail(
                        createBookingRequest.getUserPhone(),
                        createBookingRequest.getUserEmail());

        if(byPhoneOrEmail.isPresent()){

            bookingRepository.save(Booking.builder()
                    .insertBookingTime(new Date())
                    .guest(createBookingRequest.getGuests())
                    .date(createBookingRequest.getDate())
                    .time(createBookingRequest.getTime())
                    .customer(byPhoneOrEmail.get())
                    .formCodeFrom(createBookingRequest.getFormCode())
                    .requests(createBookingRequest.getParticularRequests())
                    .branchCode(createBookingRequest.getBranchCode())
                    .build());
        }else{

            Customer customerSaved = customerRepository.save(Customer.builder()
                    .customerId(0L)
                    .dob(createBookingRequest.getUserDOB())
                    .email(createBookingRequest.getUserEmail())
                    .phone(createBookingRequest.getUserPhone())
                    .name(createBookingRequest.getUserName())
                    .lastname(createBookingRequest.getUserLastName())
                    .branchCode(createBookingRequest.getBranchCode())
                    .registrationDate(new Date())
                    .treatmentPersonalData(createBookingRequest.isTreatmentPersonalData())
                    .build());

            Booking bookingSaved = bookingRepository.save(Booking.builder()
                    .insertBookingTime(new Date())
                    .guest(createBookingRequest.getGuests())
                    .date(createBookingRequest.getDate())
                    .time(createBookingRequest.getTime())
                    .customer(customerSaved)
                    .formCodeFrom(createBookingRequest.getFormCode())
                    .requests(createBookingRequest.getParticularRequests())
                    .branchCode(createBookingRequest.getBranchCode())
                    .build());

            //TODO: send message to admin
        }
    }

    public void switchIsClosedValueToBranchTimeRange(Long branchTimeRangeId){

        Optional<BranchTimeRange> branchTimeRangeRepositoryById = branchTimeRangeRepository.findById(branchTimeRangeId);
        if(branchTimeRangeRepositoryById.isPresent()){
            branchTimeRangeRepositoryById.get().setClosed(!branchTimeRangeRepositoryById.get().isClosed());
        }
    }

}
