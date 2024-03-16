package ventimetriconsulting;

import com.ventimetriconsulting.VentiMetriServiceApplication;
import com.ventimetriconsulting.branch.configuration.bookingconf.controller.BookingController;
import com.ventimetriconsulting.branch.configuration.bookingconf.entity.FormTag;
import com.ventimetriconsulting.branch.configuration.bookingconf.entity.booking.Customer;
import com.ventimetriconsulting.branch.configuration.bookingconf.entity.booking.dto.BookingDTO;
import com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto.*;
import com.ventimetriconsulting.branch.configuration.bookingconf.repository.*;
import com.ventimetriconsulting.branch.configuration.bookingconf.service.BookingService;
import com.ventimetriconsulting.branch.configuration.bookingconf.service.BranchConfigurationService;
import com.ventimetriconsulting.branch.configuration.waapiconf.service.WaApiService;
import com.ventimetriconsulting.branch.controller.BranchController;
import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.entity.Role;
import com.ventimetriconsulting.branch.entity.dto.BranchCreationEntity;
import com.ventimetriconsulting.branch.entity.dto.BranchType;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.branch.repository.BranchUserRepository;
import com.ventimetriconsulting.branch.service.BranchService;
import com.ventimetriconsulting.supplier.controller.SupplierController;
import com.ventimetriconsulting.supplier.dto.ProductDTO;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import com.ventimetriconsulting.supplier.entity.Product;
import com.ventimetriconsulting.supplier.entity.UnitMeasure;
import com.ventimetriconsulting.supplier.repository.ProductRepository;
import com.ventimetriconsulting.supplier.repository.SupplierRepository;
import com.ventimetriconsulting.supplier.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.ventimetriconsulting.branch.configuration.bookingconf.entity.BookingForm.FormType.BOOKING_FORM;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DataJpaTest
@ContextConfiguration(classes = VentiMetriServiceApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Slf4j
public class TestSuiteVentiMetriQuadriService {

    private static final Random RANDOM = new Random();


    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private BranchUserRepository branchUserRepository;

    @MockBean
    private WaApiService waApiServiceMock;

    @MockBean
    private BranchConfigurationService branchConfigurationService;

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

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;
    private BookingController bookingController;
    private BranchController  branchController;
    private SupplierController supplierController;

    @BeforeEach
    public void init(){

        BranchService branchService = new BranchService(branchRepository, branchUserRepository);
        branchController = new BranchController(branchService);

        BookingService bookingService = new BookingService(
                branchRepository,
                branchConfigurationRepository,
                branchTimeRangeRepository,
                bookingRepository,
                customerRepository,
                waApiServiceMock,
                branchConfigurationService);
        bookingController = new BookingController(bookingService);

        SupplierService supplierService = new SupplierService(productRepository, supplierRepository, branchRepository);
        supplierController = new SupplierController(supplierService);
    }

    private final String INSTANCE_CODE = "9999";


    @Test
    public void testBranchCreation() {

        String userCode = "USERCODE10";
        ResponseEntity<BranchResponseEntity> saveBranchResponseEntityResponseEntity
                = branchController.save(createFakeBranchCreationEntity(userCode));

        assertTrue(saveBranchResponseEntityResponseEntity.getStatusCode().is2xxSuccessful());
        assertEquals(Objects.requireNonNull(saveBranchResponseEntityResponseEntity.getBody()).getRole(), Role.PROPRIETARIO);
        assertEquals(Objects.requireNonNull(saveBranchResponseEntityResponseEntity.getBody()).getEmail(), "testbranch@example.com");
        assertEquals(Objects.requireNonNull(saveBranchResponseEntityResponseEntity.getBody()).getName(), "Test Branch");
        assertEquals(Objects.requireNonNull(saveBranchResponseEntityResponseEntity.getBody()).getBranchCode().length(), 10 );
        assertNotNull(Objects.requireNonNull(saveBranchResponseEntityResponseEntity.getBody()).getBranchCode());
        assertNull(Objects.requireNonNull(saveBranchResponseEntityResponseEntity.getBody()).getBranchConfigurationDTO());

        BranchResponseEntity branchResponseEntity = saveBranchResponseEntityResponseEntity.getBody();

        when(waApiServiceMock.createInstance()).thenReturn(TestUtils.convertJsonToCreateUpdateResponse(getCreateInstanceResponseOK));
        when(waApiServiceMock.retrieveClientInfo(INSTANCE_CODE)).thenReturn(TestUtils.convertMeResponse(getRetrieveBasicClientInfoErrorQrCodeStatus));

        when(waApiServiceMock.retrieveQrCode(INSTANCE_CODE)).thenReturn(TestUtils.convertQrResponse(retrieveQrCodeResponse));

        ResponseEntity<BranchConfigurationDTO> branchConfigurationDTOEntity = bookingController.configureNumberForWhatsAppMessaging(
                saveBranchResponseEntityResponseEntity.getBody().getBranchCode()
        );

        Optional<Branch> byBranchCode = branchRepository.findByBranchCode(saveBranchResponseEntityResponseEntity
                .getBody()
                .getBranchCode());

        assertEquals("instance has to be in ready status to perform this request", byBranchCode.get().getBranchConfiguration().getExplanation());
        assertNotNull(byBranchCode.get().getBranchConfiguration());
        assertEquals("qr", byBranchCode.get().getBranchConfiguration().getInstanceStatus());
        assertEquals("amati.angelo90@gmail.com", byBranchCode.get().getBranchConfiguration().getOwner());
        assertEquals("", byBranchCode.get().getBranchConfiguration().getContactId());
        assertEquals("", byBranchCode.get().getBranchConfiguration().getDisplayName());

        assertEquals(7, byBranchCode.get().getBranchConfiguration().getBookingForms().get(0).getBranchTimeRanges().size());
        assertEquals(2, byBranchCode.get().getBranchConfiguration().getTags().size());


        //simuliamo scan qr code from whats'app
        when(waApiServiceMock.retrieveClientInfo(INSTANCE_CODE)).thenReturn(TestUtils.convertMeResponse(getGetRetrieveBasicClientInfoAfterQrScan));

        String branchCode = byBranchCode.get().getBranchCode();

        BranchConfigurationDTO branchConfigurationDTOAfterScanQR = bookingController.checkWaApiStatus(branchCode);

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
            assertEquals(LocalTime.of(2, 30), branchTimeRangeDTO.getTimeRanges().get(0).getStartTime());
            assertEquals(LocalTime.of(23, 30), branchTimeRangeDTO.getTimeRanges().get(0).getEndTime());
        }

        BranchConfigurationDTO branchConfigurationAfterConfigureOpening = bookingController.updateConfiguration(BranchGeneralConfigurationEditRequest.builder()
                .guests(13)
                .bookingSlotInMinutes(30)
                .maxTableNumber(30)
                .isReservationConfirmedManually(true)
                .branchCode(branchCode)
                .dogsAllowed(15)
                .guestReceivingAuthConfirm(20)
                .minBeforeSendConfirmMessage(30)
                .build());

        assertEquals("", branchConfigurationAfterConfigureOpening.getExplanation());
        assertEquals("success", branchConfigurationAfterConfigureOpening.getInstanceStatus());
        assertEquals(1, branchConfigurationAfterConfigureOpening.getBookingFormList().size());
        assertEquals("amati.angelo90@gmail.com", branchConfigurationAfterConfigureOpening.getOwner());
        assertEquals("393454937047@c.us", branchConfigurationAfterConfigureOpening.getContactId());
        assertEquals("+39 345 493 7047", branchConfigurationAfterConfigureOpening.getFormattedNumber());
        assertEquals(15, branchConfigurationAfterConfigureOpening.getDogsAllowed());
        assertEquals("Angelo Amati", branchConfigurationAfterConfigureOpening.getDisplayName());
        assertEquals(BOOKING_FORM, branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormType());
        assertEquals(7, branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getBranchTimeRanges().size());

        // prepare here a list of BranchTime entity, i will use to update to isClosed=false (in order to open it). After that i will check if is been opened

        List<Long> branchTimeIds = new ArrayList<>();

        for( BranchTimeRangeDTO branchTimeRangeDTO : branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getBranchTimeRanges()){
            assertTrue(branchTimeRangeDTO.isClosed());
            branchTimeIds.add(branchTimeRangeDTO.getId());
            bookingController.switchIsClosedBranchTime(branchTimeRangeDTO.getId());
        }

        branchConfigurationAfterConfigureOpening = bookingController.checkWaApiStatus(branchCode);

        for( BranchTimeRangeDTO branchTimeRangeDTO : branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getBranchTimeRanges()){
            assertFalse(branchTimeRangeDTO.isClosed());
        }


        assertEquals(2, branchConfigurationAfterConfigureOpening.getTags().size());
        assertEquals(13, branchConfigurationAfterConfigureOpening.getGuests());
        assertEquals(20, branchConfigurationAfterConfigureOpening.getGuestReceivingAuthConfirm());
        assertEquals(30, branchConfigurationAfterConfigureOpening.getMinBeforeSendConfirmMessage());
        assertEquals(30, branchConfigurationAfterConfigureOpening.getMaxTableNumber());
        assertTrue(branchConfigurationAfterConfigureOpening.isReservationConfirmedManually());



        BranchResponseEntity mockResponse = BranchResponseEntity.builder()
                .branchCode(branchCode)
                .address("Via dal cazzo")
                .name("Coglionazzo")
                .email("amati.angelo90@gmail.com")
                .phone("34532134234")
                .logoImage(null)
                .build();

        when(branchConfigurationService.retrieveBranchResponseEntity(branchCode)).thenReturn(mockResponse);
        when(waApiServiceMock.retrievePhoto(anyString(), anyString())).thenReturn("https://pps.whatsapp.net/v/t61.24694-24/414551696_646621444157815_2604241172986211136_n.jpg?ccb=11-4&oh=01_AdR6nKB4IW_e2zzis8nAKK2cMg0iDHlmLaEB441dTIvL9w&oe=65CC6C26&_nc_sid=e6ed6c&_nc_cat=103");


        CustomerResult customerResult = bookingController.retrieveCustomerAndSendOtp(branchCode, "39", "3454937047");

        assertFalse(customerResult.isCustomerFound());
        assertEquals(4,customerResult.getOpt().length());
        assertNull(customerResult.getCustomer());

        bookingController
                .registerCustomer(branchCode, "Angelo", "Amati",
                        "amati.angeloooo@gmail.com", "39",
                        "3454937047", LocalDate.of(1990,
                                5, 19), true,
                        "https://pps.whatsapp.net/v/t61.24694-24/414551696_646621444157815_2604241172986211136_n.jpg?ccb=11-4&oh=01_AdR6nKB4IW_e2zzis8nAKK2cMg0iDHlmLaEB441dTIvL9w&oe=65CC6C26&_nc_sid=e6ed6c&_nc_cat=103");

        List<Customer> allCustomers = customerRepository.findAll();
        assertEquals(1, allCustomers.size());
        CustomerResult newCustomerRes = bookingController.retrieveCustomerAndSendOtp(branchCode, "39", "3454937047");

        assertTrue(newCustomerRes.isCustomerFound());
        assertEquals("amati.angeloooo@gmail.com", newCustomerRes.getCustomer().getEmail());
        assertEquals("Angelo", newCustomerRes.getCustomer().getName());
        assertNotNull(newCustomerRes.getCustomer().getImageProfile());
        assertEquals("Amati", newCustomerRes.getCustomer().getLastname());
        assertEquals("39", newCustomerRes.getCustomer().getPrefix());
        assertEquals("3454937047", newCustomerRes.getCustomer().getPhone());

        CustomerFormData customerFormData = bookingController.retrieveFormData(branchCode,
                branchConfigurationAfterConfigureOpening.getBookingFormList().get(0).getFormCode());

        log.info("Customer Form data {}", customerFormData );

        bookingController.createBooking(CreateBookingRequest.builder()
                .branchAddress("Via dal cazzo 12")
                .branchCode(branchCode)
                .branchName("20m2 Cisternino")
                .dogsAllowed(4)
                .guests(24)
                .customerId(newCustomerRes.getCustomer().getCustomerId())
                .child(0)
                .particularRequests("particular requests")
                .time("12:30")
                .date("20240404")
                .build());

        List<BookingDTO> listResponseEntity = bookingController.retrieveBookingsByBranchCode(branchCode, LocalDate.now(), null);

        assertEquals(1, listResponseEntity.size());
        assertEquals("https://pps.whatsapp.net/v/t61.24694-24/414551696_646621444157815_2604241172986211136_n.jpg?ccb=11-4&oh=01_AdR6nKB4IW_e2zzis8nAKK2cMg0iDHlmLaEB441dTIvL9w&oe=65CC6C26&_nc_sid=e6ed6c&_nc_cat=103", listResponseEntity.get(0).getCustomer().getImageProfile());

        //Create supplier and products


        ResponseEntity<SupplierDTO> supplierDTOResponseEntity = supplierController.addSupplier(createTestSupplierDTO("supplier"), branchCode);
        assertNotNull(supplierDTOResponseEntity);
        assertEquals(supplierDTOResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
        assertEquals(Objects.requireNonNull(supplierDTOResponseEntity.getBody()).getAddress(), "123 Test Address");

        ResponseEntity<ProductDTO> productDTOResponseEntity = supplierController.addProduct(createRandomInstance("Product Name"),
                Objects.requireNonNull(supplierDTOResponseEntity.getBody()).getSupplierId());

        assertEquals("Product Name", Objects.requireNonNull(productDTOResponseEntity.getBody()).getName() );
        Optional<Product> productOptional = productRepository.findById(productDTOResponseEntity.getBody().getProductId());
        assertTrue(productOptional.isPresent());
        assertEquals("Product Name" ,productOptional.get().getName());
        assertEquals(1, byBranchCode.get().getSuppliers().size());
        assertEquals(1, byBranchCode.get().getSuppliers().stream().toList().get(0).getProducts().size());
        assertEquals("Product Name", byBranchCode.get().getSuppliers().stream().toList().get(0).getProducts().stream().toList().get(0).getName());

        assertEquals(1, byBranchCode.get().getSuppliers().stream().toList().get(0).getProducts().size());

        ResponseEntity<BranchResponseEntity> branchResponseEntityResponseEntity = branchController.getBranch(userCode, branchCode);
        assertEquals(1, Objects.requireNonNull(branchResponseEntityResponseEntity.getBody()).getSupplierDTOList().size());

        //DELETE supplier
        ResponseEntity<Boolean> booleanResponseEntity = supplierController
                .unlinkSupplierFromBranch(
                        branchResponseEntityResponseEntity.getBody().getSupplierDTOList().get(0).getSupplierId(),
                        byBranchCode.get().getBranchId());

        assertEquals(HttpStatusCode.valueOf(200), booleanResponseEntity.getStatusCode());

        ResponseEntity<BranchResponseEntity> branchResponseEntityResponseEntity1 = branchController.getBranch(userCode, branchCode);
        assertEquals(0, Objects.requireNonNull(branchResponseEntityResponseEntity1.getBody()).getSupplierDTOList().size());
    }

    public static ProductDTO createRandomInstance(String productName) {
        return ProductDTO.builder()
                .productId(0)
                .name(productName)
                .unitMeasure(UnitMeasure.KG)
                .vatApplied((int) (Math.random() * 100))
                .price(Math.random() * 1000)
                .productCode("RANDOM_PRODUCT_CODE")
                .description("Random description")
                .category("Random category")
                .sku("Random SKU")
                .build();
    }

    public static SupplierDTO createTestSupplierDTO(String name) {
        SupplierDTO dto = new SupplierDTO();
        dto.setName(name);
        dto.setVatNumber("123456789");
        dto.setAddress("123 Test Address");
        dto.setCity("Test City");
        dto.setCap("12345");
        dto.setPhoneNumber("123-456-7890");
        dto.setEmail("test@supplier.com");
        dto.setPec("test@pec.supplier.com");
        dto.setCf("CF123456789");
        dto.setCountry("Test Country");
        dto.setCreatedByUserId(1L);
        return dto;
    }

    private BranchCreationEntity createFakeBranchCreationEntity(String usercode) {
        return BranchCreationEntity.builder()
                .branchCode("")
                .userCode(usercode)
                .name("Test Branch")
                .email("testbranch@example.com")
                .address("123 Test St.")
                .city("Test City")
                .cap("12345")
                .phoneNumber("123-456-7890")
                .vat("VAT123456")
                .type(BranchType.RESTAURANT)
                .logoImage(new byte[]{/* byte array for logo image */})
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
                .startTimeHour(2)
                .startTimeMinutes(30)
                .endTimeHour(23)
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
            "      \"qr_code\": \"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAARQAAAEUCAYAAADqcMl5AAAAAklEQVR4AewaftIAABJTSURBVO3BQY7YypLAQFLo+1+Z42WuChBUbb/5yAj7g7XWuuBhrbUueVhrrUse1lrrkoe11rrkYa21LnlYa61LHtZa65KHtda65GGttS55WGutSx7WWuuSh7XWuuRhrbUueVhrrUse1lrrkh8+UvmbKk5U3qiYVE4qJpWp4m9SmSreUPmi4g2VqeImlZOKE5WpYlI5qThROamYVP6mii8e1lrrkoe11rrkYa21LvnhsoqbVL6omFROKiaVL1SmikllqphU3lCZKiaVNyr+JpWTijcqJpWTikllqjhRmSpuqrhJ5aaHtda65GGttS55WGutS374ZSpvVLyhMlW8oXJScVJxUjGpTBUnFW+onFRMKlPFpHJScaIyVUwqb6j8SypTxVQxqfwmlTcqftPDWmtd8rDWWpc8rLXWJT/8j1E5qXhD5Y2KL1ROKm6qmFSmijdUTlROKiaVqeKmikllqjhRmSpOKiaV/yUPa611ycNaa13ysNZal/zwP67iRGWqOKl4Q+VE5aRiUpkqvlA5UXmjYlI5qZhUTlSmikllqpgqJpU3VKaKSeVEZar4X/Kw1lqXPKy11iUPa611yQ+/rOL/E5WpYlI5qZgqJpWbVKaKE5WTijdUJpWp4kRlqnhDZaqYVE4qTlSmiknljYqbKv5LHtZa65KHtda65GGttS754TKVf6liUpkqTiomlaliUjlRmSomlaliUpkqJpUTlaliUjlRmSpOKiaVqeINlanii4pJZar4omJSOVGZKk5U/sse1lrrkoe11rrkYa21LrE/+H9MZaqYVKaK/xKVk4o3VE4qJpWp4g2Vk4pJ5V+qmFROKt5QmSpOVKaK/88e1lrrkoe11rrkYa21LvnhI5WpYlK5qWKqmFSmihOVmyomlaliqjhRmSomlaniROVE5YuKSeWNikllqjhRmSomlaniC5U3VN5QuaniNz2stdYlD2utdcnDWmtdYn/wF6lMFV+oTBWTyknFTSpTxYnKGxVvqNxUMalMFW+onFRMKlPFicpUMalMFScqb1S8oXJSMal8UXHTw1prXfKw1lqXPKy11iX2B/+QylQxqXxRMamcVHyhMlWcqEwVk8pJxRsqU8WkMlVMKlPFicpJxaRyU8WkMlV8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\"\n" +
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
