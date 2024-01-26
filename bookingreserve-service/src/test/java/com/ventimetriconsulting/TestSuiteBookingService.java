package com.ventimetriconsulting;

import com.venticonsulting.BookingReserveServiceApplication;
import com.venticonsulting.branchconf.bookingconf.controller.BookingController;
import com.venticonsulting.branchconf.bookingconf.entity.booking.Booking;
import com.venticonsulting.branchconf.bookingconf.entity.configuration.BookingForm;
import com.venticonsulting.branchconf.bookingconf.entity.configuration.BranchConfiguration;
import com.venticonsulting.branchconf.bookingconf.entity.configuration.FormTag;
import com.venticonsulting.branchconf.bookingconf.entity.dto.*;
import com.venticonsulting.branchconf.bookingconf.repository.*;
import com.venticonsulting.branchconf.bookingconf.service.BookingService;
import com.venticonsulting.branchconf.waapiconf.service.WaApiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.venticonsulting.branchconf.bookingconf.entity.configuration.BookingForm.FormType.BOOKING_FORM;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
//@SpringBootTest
@ContextConfiguration(classes = BookingReserveServiceApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Slf4j
//@WebMvcTest(BookingController.class)
public class TestSuiteBookingService {

    private static final Random RANDOM = new Random();

    //    @Autowired
//    private MockMvc mockMvc;
    @MockBean
    private WaApiService waApiServiceMock;


    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingFormRepository bookingFormRepository;

    @Autowired
    private BranchConfigurationRepository branchConfigurationRepository;

    @Autowired
    private BranchTimeRangeRepository branchTimeRangeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private BookingController bookingController;

    @BeforeEach
    public void init(){

        BookingService bookingService = new BookingService(
                branchConfigurationRepository,
                bookingFormRepository,
                waApiServiceMock,
                branchTimeRangeRepository,
                customerRepository,
                bookingRepository);


        bookingController = new BookingController(bookingService);
    }

    private final String INSTANCE_CODE = "9999";

    @Test
    public void testCreateBranchConf(){

        String branchCode = TestUtils.generateUniqueHexCode();

        Mockito.when(waApiServiceMock.createInstance()).thenReturn(TestUtils.convertJsonToCreateUpdateResponse(getCreateInstanceResponseOK));
        Mockito.when(waApiServiceMock.retrieveClientInfo(INSTANCE_CODE)).thenReturn(TestUtils.convertMeResponse(getRetrieveBasicClientInfoErrorQrCodeStatus));

        Mockito.when(waApiServiceMock.retrieveQrCode(INSTANCE_CODE)).thenReturn(TestUtils.convertQrResponse(retrieveQrCodeResponse));

        BranchConfigurationDTO branchConfigurationDTO = bookingController.configureNumberForWhatsAppMessaging(
                branchCode
        );

//        assertEquals(2, branchConfigurationDTO.getTags().size());
        assertEquals(branchCode, branchConfigurationDTO.getBranchCode());


//        assertEquals(2, branchConfigurationDTO1.getTags().size());
        assertEquals(branchCode, branchConfigurationDTO.getBranchCode());
        assertEquals("instance has to be in ready status to perform this request", branchConfigurationDTO.getExplanation());
        assertEquals("qr", branchConfigurationDTO.getInstanceStatus());
        assertEquals("amati.angelo90@gmail.com", branchConfigurationDTO.getOwner());
        assertEquals("", branchConfigurationDTO.getContactId());
        assertEquals("", branchConfigurationDTO.getDisplayName());
        assertEquals("amati.angelo90@gmail.com", branchConfigurationDTO.getOwner());

        assertEquals(7, branchConfigurationDTO.getBookingFormList().get(0).getBranchTimeRanges().size());
        assertEquals(2, branchConfigurationDTO.getTags().size());

        //simuliamo scan qr code from whats'app
        Mockito.when(waApiServiceMock.retrieveClientInfo(INSTANCE_CODE)).thenReturn(TestUtils.convertMeResponse(getGetRetrieveBasicClientInfoAfterQrScan));

        BranchConfigurationDTO branchConfigurationDTOAfterScanQR = bookingController.checkWaApiStatus(branchCode);

        assertEquals(branchCode, branchConfigurationDTOAfterScanQR.getBranchCode());
        assertEquals("", branchConfigurationDTOAfterScanQR.getExplanation());
        assertEquals("success", branchConfigurationDTOAfterScanQR.getInstanceStatus());
        assertEquals(1, branchConfigurationDTOAfterScanQR.getBookingFormList().size());
        assertEquals("amati.angelo90@gmail.com", branchConfigurationDTOAfterScanQR.getOwner());
        assertEquals("393454937047@c.us", branchConfigurationDTOAfterScanQR.getContactId());
        assertEquals("+39 345 493 7047", branchConfigurationDTOAfterScanQR.getFormattedNumber());
        assertEquals("Angelo Amati", branchConfigurationDTOAfterScanQR.getDisplayName());
        assertEquals(BOOKING_FORM, branchConfigurationDTOAfterScanQR.getBookingFormList().get(0).getFormType());
        assertEquals(7, branchConfigurationDTOAfterScanQR.getBookingFormList().get(0).getBranchTimeRanges().size());
        assertEquals(2, branchConfigurationDTOAfterScanQR.getTags().size());


    }


    @Test
    public void testCreateBranchConfAndStoreTimeRange(){

        String branchCode = TestUtils.generateUniqueHexCode();

        Mockito.when(waApiServiceMock.createInstance()).thenReturn(TestUtils.convertJsonToCreateUpdateResponse(getCreateInstanceResponseOK));
        Mockito.when(waApiServiceMock.retrieveClientInfo(INSTANCE_CODE)).thenReturn(TestUtils.convertMeResponse(getRetrieveBasicClientInfoErrorQrCodeStatus));

        Mockito.when(waApiServiceMock.retrieveQrCode(INSTANCE_CODE)).thenReturn(TestUtils.convertQrResponse(retrieveQrCodeResponse));

        BranchConfigurationDTO branchConfigurationDTO = bookingController.configureNumberForWhatsAppMessaging(
                branchCode
        );

        assertEquals(branchCode, branchConfigurationDTO.getBranchCode());
        assertEquals(branchCode, branchConfigurationDTO.getBranchCode());
        assertEquals("instance has to be in ready status to perform this request", branchConfigurationDTO.getExplanation());
        assertEquals("qr", branchConfigurationDTO.getInstanceStatus());
        assertEquals("amati.angelo90@gmail.com", branchConfigurationDTO.getOwner());
        assertEquals("", branchConfigurationDTO.getContactId());
        assertEquals("", branchConfigurationDTO.getDisplayName());

        assertEquals(7, branchConfigurationDTO.getBookingFormList().get(0).getBranchTimeRanges().size());
        assertEquals(2, branchConfigurationDTO.getTags().size());

        //simuliamo scan qr code from whats'app
        Mockito.when(waApiServiceMock.retrieveClientInfo(INSTANCE_CODE)).thenReturn(TestUtils.convertMeResponse(getGetRetrieveBasicClientInfoAfterQrScan));

        BranchConfigurationDTO branchConfigurationDTOAfterScanQR = bookingController.checkWaApiStatus(branchCode);

        assertEquals(branchCode, branchConfigurationDTOAfterScanQR.getBranchCode());
        assertEquals("", branchConfigurationDTOAfterScanQR.getExplanation());
        assertEquals("success", branchConfigurationDTOAfterScanQR.getInstanceStatus());
        assertFalse(branchConfigurationDTOAfterScanQR.isReservationConfirmedManually());
        assertEquals(1, branchConfigurationDTOAfterScanQR.getBookingFormList().size());
        assertEquals("amati.angelo90@gmail.com", branchConfigurationDTOAfterScanQR.getOwner());
        assertEquals("393454937047@c.us", branchConfigurationDTOAfterScanQR.getContactId());
        assertEquals("+39 345 493 7047", branchConfigurationDTOAfterScanQR.getFormattedNumber());
        assertEquals("Angelo Amati", branchConfigurationDTOAfterScanQR.getDisplayName());
        assertEquals(BOOKING_FORM, branchConfigurationDTOAfterScanQR.getBookingFormList().get(0).getFormType());
        assertEquals(7, branchConfigurationDTOAfterScanQR.getBookingFormList().get(0).getBranchTimeRanges().size());
        assertEquals(2, branchConfigurationDTOAfterScanQR.getTags().size());

        List<Long> timeRangeIds = new ArrayList<>();

        for(BranchTimeRangeDTO branchTimeRangeDTO : branchConfigurationDTOAfterScanQR.getBookingFormList().get(0).getBranchTimeRanges()){

            timeRangeIds.add(branchTimeRangeDTO.getId());

            assertEquals(0, branchTimeRangeDTO.getTimeRanges().size());
            assertEquals(true, branchTimeRangeDTO.isClosed());

        }

        //now lets create a UpdateBranchConfigurationRequest

        BranchConfigurationDTO branchConfigurationDTO1 = bookingController.updateTimeRange(UpdateBranchTimeRanges.builder()
                .listConfIds(timeRangeIds)
                .branchCode(branchCode)
                .timeRanges(buildDefaultTimeRangeList())
                .build());

        log.info("Added time ranges");

        assertEquals("Form Default", branchConfigurationDTO1.getBookingFormList().get(0).getFormName());
        assertTrue(branchConfigurationDTO1.getBookingFormList().get(0).isDefaultForm());

        for(BranchTimeRangeDTO branchTimeRangeDTO : branchConfigurationDTO1.getBookingFormList().get(0).getBranchTimeRanges()){
            assertEquals(1, branchTimeRangeDTO.getTimeRanges().size());
            assertEquals(LocalTime.of(8, 30), branchTimeRangeDTO.getTimeRanges().get(0).getStartTime());
            assertEquals(LocalTime.of(12, 30), branchTimeRangeDTO.getTimeRanges().get(0).getEndTime());
        }

        BranchConfigurationDTO branchConfigurationAfterConfigureOpening = bookingController.updateConfiguration(BranchGeneralConfigurationEditRequest.builder()
                .guests(13)
                .bookingSlotInMinutes(30)
                .maxTableNumber(30)
                .isReservationConfirmedManually(true)
                .branchCode(branchCode)
                .guestReceivingAuthConfirm(20)
                .minBeforeSendConfirmMessage(30)
                .build());

        assertEquals(branchCode, branchConfigurationAfterConfigureOpening.getBranchCode());
        assertEquals("", branchConfigurationAfterConfigureOpening.getExplanation());
        assertEquals("success", branchConfigurationAfterConfigureOpening.getInstanceStatus());
        assertEquals(1, branchConfigurationAfterConfigureOpening.getBookingFormList().size());
        assertEquals("amati.angelo90@gmail.com", branchConfigurationAfterConfigureOpening.getOwner());
        assertEquals("393454937047@c.us", branchConfigurationAfterConfigureOpening.getContactId());
        assertEquals("+39 345 493 7047", branchConfigurationAfterConfigureOpening.getFormattedNumber());
        assertEquals("Angelo Amati", branchConfigurationAfterConfigureOpening.getDisplayName());
        assertEquals(BOOKING_FORM, branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormType());
        assertEquals(7, branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getBranchTimeRanges().size());
        assertEquals(2, branchConfigurationAfterConfigureOpening.getTags().size());
        assertEquals(13, branchConfigurationAfterConfigureOpening.getGuests());
        assertEquals(20, branchConfigurationAfterConfigureOpening.getGuestReceivingAuthConfirm());
        assertEquals(30, branchConfigurationAfterConfigureOpening.getMinBeforeSendConfirmMessage());
        assertEquals(30, branchConfigurationAfterConfigureOpening.getMaxTableNumber());
        assertTrue(branchConfigurationAfterConfigureOpening.isReservationConfirmedManually());

        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));
        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));
        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));
        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));
        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));
        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));
        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));
        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));
        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));
        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));
        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));
        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));
        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));
        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));
        bookingController.createBooking(createRandomBookingRequest(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode()));

        CustomerFormData customerFormData = bookingController.retrieveFormData(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode());

        log.info("");

    }

    public static CreateBookingRequest createRandomBookingRequest(String brancCode, String formCode) {
        return CreateBookingRequest.builder()
                .branchCode(brancCode)
                .formCode(formCode)
                .userEmail(generateRandomEmail())
                .userPhone(generateRandomPhoneNumber())
                .userName(generateRandomString(5))
                .userLastName(generateRandomString(8))
                .userDOB(generateRandomDateOfBirth())
                .guests(RANDOM.nextInt(10) + 1) // Random number between 1 and 10
                .particularRequests(generateRandomString(20))
                .date(LocalDate.now().plusDays(RANDOM.nextInt(7))) // Random date within the next 7 days
                .time(LocalTime.of(RANDOM.nextInt(24), 0)) // Random time
                .treatmentPersonalData(RANDOM.nextBoolean())
                .build();
    }

    private static String generateRandomString(int length) {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char randomChar = chars[RANDOM.nextInt(chars.length)];
            sb.append(randomChar);
        }
        return sb.toString();
    }

    private static String generateRandomEmail() {
        return generateRandomString(8) + "@example.com";
    }

    private static String generateRandomPhoneNumber() {
        return "+1-" + RANDOM.nextInt(1000) + "-" + RANDOM.nextInt(1000) + "-" + RANDOM.nextInt(10000);
    }

    private static LocalDate generateRandomDateOfBirth() {
        return LocalDate.now().minusYears(RANDOM.nextInt(30) + 18); // Random date of birth for an adult
    }



    private List<TimeRangeUpdateRequest> buildDefaultTimeRangeList() {
        List<TimeRangeUpdateRequest> timeRangeUpdateRequestList = new ArrayList<>();
        timeRangeUpdateRequestList.add(TimeRangeUpdateRequest.builder()
                .startTimeHour(8)
                .startTimeMinutes(30)
                .endTimeHour(12)
                .endTimeMinutes(30).
                build());

        return timeRangeUpdateRequestList;
    }


    String getCreateInstanceResponseOK = "{\n" +
            "  \"instance\": {\n" +
            "    \"id\": "+ INSTANCE_CODE + ",\n" +
            "    \"owner\": \"amati.angelo90@gmail.com\",\n" +
            "    \"webhook_url\": null,\n" +
            "    \"webhook_events\": [],\n" +
            "    \"is_trial\": null\n" +
            "  },\n" +
            "  \"status\": \"success\"\n" +
            "}";

    String getCreateInstanceKO = "You reached your instance limit. Please update your subscription to create instances.";

    String getRetrieveBasicClientInfoErrorQrCodeStatus = "{\n" +
            "  \"me\": {\n" +
            "    \"status\": \"error\",\n" +
            "    \"message\": \"instance not ready\",\n" +
            "    \"instanceId\": \""+ INSTANCE_CODE +"\",\n" +
            "    \"explanation\": \"instance has to be in ready status to perform this request\",\n" +
            "    \"instanceStatus\": \"qr\"\n" +
            "  },\n" +
            "  \"links\": {\n" +
            "    \"self\": \"https://waapi.app/api/v1/instances/" + INSTANCE_CODE + "/client/me\"\n" +
            "  },\n" +
            "  \"status\": \"success\"\n" +
            "}";

    String retrieveQrCodeResponse = "{\n" +
            "  \"qrCode\": {\n" +
            "    \"status\": \"success\",\n" +
            "    \"instanceId\": \""+ INSTANCE_CODE+"\",\n" +
            "    \"data\": {\n" +
            "      \"qr_code\": \"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAARQAAAEUCAYAAADqcMl5AAAAAklEQVR4AewaftIAABJTSURBVO3BQY7YypLAQFLo+1+Z42WuChBUbb/5yAj7g7XWuuBhrbUueVhrrUse1lrrkoe11rrkYa21LnlYa61LHtZa65KHtda65GGttS55WGutSx7WWuuSh7XWuuRhrbUueVhrrUse1lrrkh8+UvmbKk5U3qiYVE4qJpWp4m9SmSreUPmi4g2VqeImlZOKE5WpYlI5qThROamYVP6mii8e1lrrkoe11rrkYa21LvnhsoqbVL6omFROKiaVL1SmikllqphU3lCZKiaVNyr+JpWTijcqJpWTikllqjhRmSpuqrhJ5aaHtda65GGttS55WGutS374ZSpvVLyhMlW8oXJScVJxUjGpTBUnFW+onFRMKlPFpHJScaIyVUwqb6j8SypTxVQxqfwmlTcqftPDWmtd8rDWWpc8rLXWJT/8j1E5qXhD5Y2KL1ROKm6qmFSmijdUTlROKiaVqeKmikllqjhRmSpOKiaV/yUPa611ycNaa13ysNZal/zwP67iRGWqOKl4Q+VE5aRiUpkqvlA5UXmjYlI5qZhUTlSmikllqpgqJpU3VKaKSeVEZar4X/Kw1lqXPKy11iUPa611yQ+/rOL/E5WpYlI5qZgqJpWbVKaKE5WTijdUJpWp4kRlqnhDZaqYVE4qTlSmiknljYqbKv5LHtZa65KHtda65GGttS754TKVf6liUpkqTiomlaliUjlRmSomlaliUpkqJpUTlaliUjlRmSpOKiaVqeINlanii4pJZar4omJSOVGZKk5U/sse1lrrkoe11rrkYa21LrE/+H9MZaqYVKaK/xKVk4o3VE4qJpWp4g2Vk4pJ5V+qmFROKt5QmSpOVKaK/88e1lrrkoe11rrkYa21LvnhI5WpYlK5qWKqmFSmihOVmyomlaliqjhRmSomlaniROVE5YuKSeWNikllqjhRmSomlaniC5U3VN5QuaniNz2stdYlD2utdcnDWmtdYn/wF6lMFV+oTBWTyknFTSpTxYnKGxVvqNxUMalMFW+onFRMKlPFicpUMalMFScqb1S8oXJSMal8UXHTw1prXfKw1lqXPKy11iX2B/+QylQxqXxRMamcVHyhMlWcqEwVk8pJxRsqU8WkMlVMKlPFicpJxaRyU8WkMlV8oTJVTCpTxRsqJxVfqEwVXzystdYlD2utdcnDWmtdYn/wF6m8UTGpnFRMKlPFicpU8YbKVDGpnFScqLxR8YXKVHGTylRxovJGxRsqJxV/k8obFScqU8VND2utdcnDWmtd8rDWWpfYH/wilaniDZU3KiaVk4oTlS8qTlTeqLhJ5aTiRGWqmFS+qPhC5aTiROWNiknljYovVKaKv+lhrbUueVhrrUse1lrrEvuDX6QyVUwqJxWTylQxqUwVJypfVNyk8l9ScZPKGxUnKjdVnKicVHyhclIxqdxU8cXDWmtd8rDWWpc8rLXWJfYHf5HKVHGiclLxhspUMalMFZPKb6o4UZkqJpWp4g2VNypOVKaKN1SmihOVk4oTld9UMamcVEwqU8UbKicVXzystdYlD2utdcnDWmtd8sNHKlPFScWkMlVMFZPKGyonKm9UnKhMFZPKb6r4m1ROKiaV31RxojJVvFFxojJVnFS8UTGpTBVvVNz0sNZalzystdYlD2utdYn9wUUqJxVvqEwVk8pJxRsqJxWTyknFpDJVTCpvVEwqU8WJylTxN6lMFZPKScWkclJxk8pU8YXKGxUnKlPFpDJVfPGw1lqXPKy11iUPa611yQ8fqZxUTConFVPFTSpTxUnFpPKbKiaVE5Wp4kRlqphUTiq+UHmj4o2KSWVSmSreUDlRmSpuqphUTiomlanipoe11rrkYa21LnlYa61LfrisYlL5QmWqOKmYVP6mipOKE5WpYlK5SeWkYlKZKiaVk4pJZVJ5o2JSmSq+UDmpOFGZKiaVqWJSOamYVCaVqWJSmSq+eFhrrUse1lrrkoe11rrE/uADlaliUvmiYlL5omJSOamYVKaKSWWqmFSmiknlpoo3VG6quEllqjhRmSomlTcqJpWp4kTli4pJZao4UZkqbnpYa61LHtZa65KHtda6xP7gIpXfVDGpvFHxhspJxYnKScWkMlVMKlPFGyonFZPKVHGTylTxhcpUcZPKGxVvqLxRMalMFX/Tw1prXfKw1lqXPKy11iU/fKTymypOKiaVN1Smir9JZaqYVN5QmSreUJkqJpWTihOVqWJSmSomlaniROWk4ouKSeW/RGWqmFSmii8e1lrrkoe11rrkYa21LrE/+EBlqjhRmSpOVKaKSWWqeENlqjhReaPiROWk4kRlqphUporfpDJVTCo3Vbyh8kbFpHJS8YbKVDGpnFS8oXJS8cXDWmtd8rDWWpc8rLXWJT/8ZRVvVEwqU8UXFW9UfKEyVZyo3KQyVUwqU8WJyhcVb6hMKlPFpHJTxRcqU8UbFZPKScXf9LDWWpc8rLXWJQ9rrXXJD79M5aRiUnlDZao4Ufmi4ouKSeUmlanijYpJZao4qTipmFROKk4qJpWTii9U/qaKSWWqOFH5mx7WWuuSh7XWuuRhrbUu+eGjijcqJpWp4iaVLypOVKaKSWWqOKmYVKaKSeVE5aTii4pJZao4qXij4qRiUplU3qg4qbhJ5aTiRGWqOFG56WGttS55WGutSx7WWusS+4MPVE4qTlTeqJhUTiomlTcq3lCZKt5Q+ZcqJpU3Kk5U3qg4UfmiYlKZKt5QOamYVL6oeEPlpOKLh7XWuuRhrbUueVhrrUvsD/4ilTcqJpWbKiaVk4ovVE4qJpWpYlJ5o+JE5aRiUnmj4g2Vk4oTlS8q3lC5qeL/k4e11rrkYa21LnlYa61LfvhIZao4qZhUTlRuqvhNKicVk8oXFZPKVDGpfKHyRsWJylQxVbyh8kbFicobFScqb6i8UTGpTBWTylTxxcNaa13ysNZalzystdYlP/wylTcq3lB5Q+WNii8qJpUvVE4qJpWpYlKZKt5QeUPlRGWq+JcqTlQmlaliqphUpoo3VE4q/qaHtda65GGttS55WGutS364TOUmlanipGJSOamYVCaVqeKmiknlpoovVKaKN1ROKk5UpoqTihOVE5UvKiaVqeINlaniRGWqmFR+08Naa13ysNZalzystdYlP3xUcaLyRcUXFZPKGxVvVEwqJypTxW9SeaPijYovVE5UpooTlROVNyomlTdU3qh4o+KkYlK56WGttS55WGutSx7WWusS+4NfpPI3Vdyk8kXFpDJVnKicVEwqU8Wk8jdVTConFZPKVPGbVG6qOFG5qWJSmSomlanii4e11rrkYa21LnlYa61LfrhM5aTiRGWqmFSmihOVk4qTiknlN6m8oTJVnFS8ofJGxUnFicpUcaJyU8WJylQxqZyoTBVvqEwVk8pUMan8poe11rrkYa21LnlYa61LfvhI5aRiUpkqpopJ5UTlpGJS+U0Vk8qJylTxhcobFZPKFypTxaQyVZyonFRMKm9UTCpTxVQxqXyh8ptU/qaHtda65GGttS55WGutS374qGJSeUNlqpgqTlTeqDhROamYVG5SmSomlZOKSeWNiknlpGJSOamYVKaKSeWNikllqvhCZaqYVCaVNyomlanipOJfelhrrUse1lrrkoe11rrkh49UTlSmikllUjmp+ELljYpJZaqYVE4qJpWpYlI5qZhUpooTld+kclIxqUwVJypTxVQxqXxRMal8UXFSMancVHHTw1prXfKw1lqXPKy11iX2B3+RyknFicpJxaQyVdyk8kbFicpUcaIyVbyh8kbFb1KZKiaVLyomlaniRGWqmFSmiknli4oTlaliUjmp+OJhrbUueVhrrUse1lrrkh8+UnmjYlKZVP4mlaliUnmjYlKZVE4qTlSmiknljYpJZap4Q2WqmFSmiqnipGJSmSomlUnlRGWqmCq+qDhROVGZKr6ouOlhrbUueVhrrUse1lrrEvuDf0hlqnhDZao4UZkqJpWpYlKZKiaVk4pJ5aaKSWWqmFSmiknlpOJEZar4TSo3VUwqU8WkclIxqbxRMalMFW+oTBVfPKy11iUPa611ycNaa11if3CRyhcVk8pJxaRyUjGpfFHxL6n8SxWTylQxqXxRcaIyVbyhclIxqZxUTCpTxaQyVfwmlanii4e11rrkYa21LnlYa61LfvjHKiaVqeJE5aaKL1S+qDhRmSq+UJkqJpU3Kt6omFSmiknlpGJSOamYKk5UvqiYVE5UTiomlaliUpkqbnpYa61LHtZa65KHtda6xP7gA5WTii9UpoqbVN6omFTeqHhDZar4QmWqOFE5qZhUpopJ5Y2KN1SmijdUTiomlf+SijdUpoovHtZa65KHtda65GGttS6xP7hIZaqYVKaKL1SmikllqnhDZao4UZkqJpWTiknlpOILlaniRGWqOFE5qZhUTiomlZOKSWWqOFGZKiaVqeJE5Y2KE5Wp4l96WGutSx7WWuuSh7XWusT+4AOVLyomlZOKSWWqeENlqjhRmSpuUpkqTlROKiaVqWJS+U0Vk8pUMamcVLyhMlVMKl9UTConFScqU8UbKlPFpDJVfPGw1lqXPKy11iUPa611if3BRSpvVEwqU8Wk8kbFpDJVTCpTxYnKf0nFicpJxaQyVbyh8psqJpWp4kRlqphUvqi4SWWqmFSmikllqrjpYa21LnlYa61LHtZa65IfPlI5qZhUTiomlaliUrmpYlI5qZhUTipOVE4q3lCZKn6TylQxqZxUnKhMKlPFpDJVnKhMFScqU8WkclIxqbyhMlWcVEwqU8UXD2utdcnDWmtd8rDWWpf88FHFpPKGylQxVZxUnKhMFZPKVHFSMamcVEwqb1ScqHyh8obKVHGiclIxqUwVU8WJylTxhcpJxRcqU8UbKm+o/KaHtda65GGttS55WGutS374SOVE5QuVk4pJZap4Q2WqOKk4UZkqJpU3VKaKSWWqeKNiUpkqTiomlaliUrmp4kRlqnhDZVKZKn6TylQxqUwVU8WkctPDWmtd8rDWWpc8rLXWJT9cVvGFylQxqUwqJyonFZPKicpJxVQxqdykMlWcqJyonKhMFScVk8pvUjmp+E0qU8UXKl+onFTc9LDWWpc8rLXWJQ9rrXWJ/cEvUvkvq5hU3qiYVKaKN1TeqJhU/qaKSWWqmFSmihOVNypOVG6qOFGZKt5Q+aJiUjmp+OJhrbUueVhrrUse1lrrEvuDD1TeqJhUpoovVH5TxaQyVUwqU8UbKm9UnKhMFScqU8VNKicVJypTxaRyUjGpTBWTyhsVJyonFTepnFR88bDWWpc8rLXWJQ9rrXXJD3+ZylRxojJVvFHxhcpJxaTyhspU8YXKVHGi8obKGxWTyhsqU8VUMal8UTGp3KTyhspNFZPKTQ9rrXXJw1prXfKw1lqX/PBRxW+qOFGZKk5UvlD5QmWqmFSmiknlN1W8ofJFxRsqJxWTylQxqUwVU8Wk8psq3lCZKiaVk4qbHtZa65KHtda65GGttS754SOVv6liqphUTireUDmpOFGZKiaVqeKNihOVL1SmipOKSWWqOFH5QmWqmFSmiknlpOJvUpkqTlSmiknlpOKLh7XWuuRhrbUueVhrrUt+uKziJpUTlaniROWk4qRiUpkqpoo3VE4qTlROKiaVk4ovKiaVk4pJZap4Q+WNii9UpoqpYlI5qbipYlK56WGttS55WGutSx7WWuuSH36ZyhsVf1PFFxWTyknFGxVfVEwqJypfqJxUTCpvqEwVk8pJxRcqb6i8oXKTyknFTQ9rrXXJw1prXfKw1lqX/PA/TmWqmFSmii8q3qg4UflC5Y2KSeWk4g2VE5Wp4o2KSWVSmSp+U8WkMlWcqEwVk8obFZPKVPHFw1prXfKw1lqXPKy11iU//I+rmFROVG5SeaPii4oTlROVqeKmikllqjhRmSomlaniC5WTikllUpkq3qiYVKaKSWWq+Jse1lrrkoe11rrkYa21Lvnhl1X8poo3KiaVqWJSOVE5qZhUTlROKiaVSWWqmComlaliUpkqbqo4UXmjYlKZKk5U3lA5qXhD5aRiUpkq/qWHtda65GGttS55WGutS364TOVvUjmpOKl4Q+U3VZyonFScqEwVb6hMFZPKFxVTxU0qU8VJxaRyUjGpvFExqUwqU8Wk8i89rLXWJQ9rrXXJw1prXWJ/sNZaFzystdYlD2utdcnDWmtd8rDWWpc8rLXWJQ9rrXXJw1prXfKw1lqXPKy11iUPa611ycNaa13ysNZalzystdYlD2utdcnDWmtd8n/po5uRonJxagAAAABJRU5ErkJggg==\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"links\": {\n" +
            "    \"self\": \"https://waapi.app/api/v1/instances/4861/client/qr\"\n" +
            "  },\n" +
            "  \"status\": \"success\"\n" +
            "}";

    String getGetRetrieveBasicClientInfoAfterQrScan = "{\n" +
            "  \"me\": {\n" +
            "    \"status\": \"success\",\n" +
            "    \"instanceId\": \"4861\",\n" +
            "    \"data\": {\n" +
            "      \"displayName\": \"Angelo Amati\",\n" +
            "      \"contactId\": \"393454937047@c.us\",\n" +
            "      \"formattedNumber\": \"+39 345 493 7047\",\n" +
            "      \"profilePicUrl\": \"https://pps.whatsapp.net/v/t61.24694-24/414551696_646621444157815_2604241172986211136_n.jpg?ccb=11-4&oh=01_AdSccD_AVA2-Ce49VXMOqF3r2IWyRRcJ9iRBtsPZ1pr3hw&oe=65BCD266&_nc_sid=e6ed6c&_nc_cat=103\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"links\": {\n" +
            "    \"self\": \"https://waapi.app/api/v1/instances/4861/client/me\"\n" +
            "  },\n" +
            "  \"status\": \"success\"\n" +
            "}";

    @Test
    public void testsssss() {

        BranchConfiguration branchConfiguration = new BranchConfiguration();
        branchConfiguration.setReservationConfirmedManually(false);
        branchConfiguration.setMinBeforeSendConfirmMessage(0);
        branchConfiguration.setGuestReceivingAuthConfirm(0);
        branchConfiguration.setGuests(0);
        branchConfiguration.setBookingSlotInMinutes(0);
        branchConfiguration.setBookingSlotInMinutes(0);
        branchConfiguration.setMaxTableNumber(0);
        branchConfiguration.setBranchCode(TestUtils.generateUniqueHexCode());
        branchConfiguration.setLastWaApiConfCheck(new Date());
        // branchConfiguration.setTags(buildDefaultTagsList()); // Uncomment if buildDefaultTagsList() is available
        branchConfiguration.setBranchConfCreationDate(new Date());
        branchConfiguration.setBookingForms(new ArrayList<>());

        BookingForm bookingForm = new BookingForm();
        bookingForm.setDefaultForm(true);
        bookingForm.setFormType(BOOKING_FORM);
        bookingForm.setRedirectPage("");
        bookingForm.setFormName("Form Default");
        bookingForm.setBranchConfiguration(branchConfiguration);
        bookingForm.setCreationDate(new Date());

        branchConfiguration.getBookingForms().add(bookingForm);

        branchConfigurationRepository.save(branchConfiguration);

        List<BookingForm> bookingForms = bookingFormRepository.findAll();
        List<BranchConfiguration> branchConf = branchConfigurationRepository.findAll();
        log.info("Test: Conf form list : " + branchConf.get(0).getBookingForms().size());
        log.info("Test: Booking form list : " + bookingForms);
    }


    private List<FormTag> buildDefaultTagsList() {
        List<FormTag> tags = new ArrayList<>();
        FormTag formTag = new FormTag();
        formTag.setTitle("Cena");
        FormTag formTag1 = new FormTag();
        formTag1.setTitle("Pranzo");
        tags.add(formTag);
        tags.add(formTag1);
        return tags;
    }

}
