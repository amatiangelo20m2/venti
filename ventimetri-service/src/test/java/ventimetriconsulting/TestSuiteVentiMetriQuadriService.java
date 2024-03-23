package ventimetriconsulting;

import com.netflix.discovery.converters.Auto;
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
import com.ventimetriconsulting.inventario.controller.StorageController;
import com.ventimetriconsulting.inventario.entity.dto.StorageDTO;
import com.ventimetriconsulting.inventario.repository.StorageRepository;
import com.ventimetriconsulting.inventario.service.StorageService;
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
import org.springframework.http.HttpStatus;
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
    private StorageRepository storageRepository;

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

    private StorageController storageController;
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

        StorageService storageService = new StorageService(storageRepository, branchRepository);
        storageController = new StorageController(storageService);
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
                .branchId(0)
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

        ResponseEntity<ProductDTO> productDTOResponseEntity = supplierController.insertProduct(createRandomInstance("Product Name"),
                Objects.requireNonNull(supplierDTOResponseEntity.getBody()).getSupplierId());

        assertEquals("Product Name", Objects.requireNonNull(productDTOResponseEntity.getBody()).getName() );
        Optional<Product> productOptional = productRepository.findById(productDTOResponseEntity.getBody().getProductId());

        assertTrue(productOptional.isPresent());
        assertEquals("Product Name" ,productOptional.get().getName());
        assertEquals("Product Name", byBranchCode.get().getSuppliers().stream().toList().get(0).getProducts().stream().toList().get(0).getName());
        assertEquals(1, byBranchCode.get().getSuppliers().size());
        assertEquals(1, byBranchCode.get().getSuppliers().stream().toList().get(0).getProducts().size());

        assertEquals(1, byBranchCode.get().getSuppliers().stream().toList().get(0).getProducts().size());

        ResponseEntity<BranchResponseEntity> branchResponseEntityResponseEntity = branchController.getBranch(userCode, branchCode);
        assertEquals(1, Objects.requireNonNull(branchResponseEntityResponseEntity.getBody()).getSupplierDTOList().size());

        long supplierId = branchResponseEntityResponseEntity.getBody().getSupplierDTOList().get(0).getSupplierId();
        long branchId = byBranchCode.get().getBranchId();

        //DELETE supplier
        ResponseEntity<Boolean> booleanResponseEntity = supplierController
                .unlinkSupplierFromBranch(
                        supplierId,
                        branchId);

        assertEquals(HttpStatusCode.valueOf(200),
                booleanResponseEntity.getStatusCode());

        ResponseEntity<BranchResponseEntity> branchResponseEntityResponseEntity1 = branchController.getBranch(userCode, branchCode);
        assertEquals(0, Objects.requireNonNull(branchResponseEntityResponseEntity1.getBody()).getSupplierDTOList().size());

        supplierController.associateSupplierToBranch(supplierId, branchId);
        branchResponseEntityResponseEntity1 = branchController.getBranch(userCode, branchCode);
        assertEquals(1, Objects.requireNonNull(branchResponseEntityResponseEntity1.getBody()).getSupplierDTOList().size());
        assertEquals(1, Objects.requireNonNull(branchResponseEntityResponseEntity1.getBody()).getSupplierDTOList().get(0).getProductDTOList().size());

        ProductDTO productDTO = branchResponseEntityResponseEntity1.getBody().getSupplierDTOList().get(0).getProductDTOList().get(0);

        assertEquals(20, productDTO.getProductCode().length());
        assertEquals("Random description", productDTO.getDescription());
        assertEquals("Random category", productDTO.getCategory());
        assertEquals("Random SKU", productDTO.getSku());
        assertEquals(UnitMeasure.KG, productDTO.getUnitMeasure());

        productDTO.setCategory("NEW CATEGORY");
        productDTO.setProductCode("NEW PROD CODE");
        productDTO.setDescription("NEW DESCRIPTION");
        productDTO.setSku("NEW SKU");
        productDTO.setName("NEW NAME");
        productDTO.setPrice(323.12);
        productDTO.setUnitMeasure(UnitMeasure.CT);

        supplierController.updateProduct(productDTO);

        productDTO = Objects.requireNonNull(branchController.getBranch(userCode, branchCode).getBody()).getSupplierDTOList().get(0).getProductDTOList().get(0);

        assertEquals("NEW PROD CODE", productDTO.getProductCode());
        assertEquals("NEW DESCRIPTION", productDTO.getDescription());
        assertEquals("NEW CATEGORY", productDTO.getCategory());
        assertEquals("NEW SKU", productDTO.getSku());
        assertEquals(UnitMeasure.CT, productDTO.getUnitMeasure());

        supplierController.deleteProductById(productDTO.getProductId(), supplierId);

        branchResponseEntityResponseEntity1 = branchController.getBranch(userCode, branchCode);
        assertEquals(0, branchResponseEntityResponseEntity1.getBody().getSupplierDTOList().get(0).getProductDTOList().size());


        ResponseEntity<StorageDTO> cisterninoStorage = storageController.addStorage(StorageDTO.builder()
                        .storageId(0)
                        .creationTime(new Date())
                        .name("Magazzino 20m2")
                        .city("Cisternino")
                        .address("Via 4 novembre 8")
                        .cap("72914")
                        .build(),
                branchCode);

        assertEquals(HttpStatusCode.valueOf(200), cisterninoStorage.getStatusCode());
        assertEquals("Magazzino 20m2", cisterninoStorage.getBody().getName());


        List<SupplierDTO> supplierDTOS = retrieveSupplierList();

        for(SupplierDTO supplierDTO : supplierDTOS){
            ResponseEntity<SupplierDTO> supplierDTOResponseEntity1 = supplierController
                    .addSupplier(supplierDTO,
                            branchCode);

            ResponseEntity<List<ProductDTO>> insertProductList = supplierController.insertProductList(
                    getPRoductListById(Integer.parseInt(supplierDTO.getVatNumber())),
                    Objects.requireNonNull(supplierDTOResponseEntity1.getBody()).getSupplierId());

            System.out.println("");
        }


        System.out.println("asdsdasd");
        branchResponseEntityResponseEntity1 = branchController.getBranch(userCode, branchCode);

        assertEquals(29, branchResponseEntityResponseEntity1.getBody().getSupplierDTOList().size());


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
//        dto.setCreatedByUserId(1L);
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


    public static List<SupplierDTO> retrieveSupplierList() {

        List<SupplierDTO> suppliersList = new ArrayList<>();
        suppliersList.add(new SupplierDTO(0, "Magazzino 20m2", "463", "", "", "", "", "", "", "", "", null));
        suppliersList.add(new SupplierDTO(0, "Angelo simeone", "464", "", "", "", "", "+393388004773", "simeoneangelo1979@libero.it", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Mattia", "466", "", "", "", "", "3803268119", "mattialiuzzi@hotmail.it", "","Italia", null));
        suppliersList.add(new SupplierDTO(0, "Santoro", "467", "", "", "35480", "", "3454937840", "amati.angelo90@gmail.com", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Fornitore Esempio", "468", "via roma 34", "Cisternino", "72014", "", "3454937077", "esempiofornitore@gmail.com", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Piero Montenegro", "469", "", "", "", "", "+393285860885", "piero.greco.ext@montenegro.it", "", "Italia",null));
        suppliersList.add(new SupplierDTO(0, "Giuseppe Martini", "470", "", "", "", "", "3459515136", "gangelillo@bacardi.com", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Fabio gal", "471", "", "", "", "", "3339101487", "galbibite@libero.it", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Alberto Velier", "472", "", "", "", "", "3485174174", "albertobiunno@gmail.com", "", "Italia",null));
        suppliersList.add(new SupplierDTO(0, "Mimmo Sifor", "473", "", "", "", "", "3382603861", "siforsrl@libero.it", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Gianluca sanitec", "474", "", "", "", "", "3334145027", "gianlucacasio@euroshopsrl.it", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Andrea Taveri", "475", "", "", "", "", "3208193807", "mattialiuzzi@hotmail.it", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Luciano Tormaresca", "476", "", "", "", "", "336274885", "carbone.luc@gmail.com", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Deborah sagna", "477", "", "", "", "", "3398820588", "debora@agenziatres.it", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Pastini", "478", "", "", "", "", "3929944659", "info@ipastini.it", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Donato Panini", "479", "via clarizia", "", "72014", "05173382", "3202518517", "valelui1408@yahoo.it", "", "Italia",null));
        suppliersList.add(new SupplierDTO(0, "Macelleria longo", "480", "", "", "", "", "3927043155", "angelopizzutoli87@gmail.com", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Domenico aia", "481", "", "", "", "", "3920730621", "domenico.pace@aia-spa.it", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Salumificio Santoro", "482", "", "", "", "", "0804431297", "ordini@salumificiodantoro.it", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Andrea Formaggi", "483", "", "", "", "", "3803632793", "info@anticaricettamartinese.it", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Cardone", "484", "", "", "", "", "3887350884", "Marianna.cardone@cardonevini.com", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "giacomoko", "485", "", "", "", "", "14784845448", "amaticorporation@gmail.com", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Fornitoreextra", "486", "", "", "", "", "3803268199", "mattialiuzzi@hotmail.it", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Frutta", "487", "", "", "", "", "3803268119", "toninocontinisio@libero.it", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Pesce", "488", "", "", "", "", "3358188937", "Giovanni.mancini@leporemare.com", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "De giorgio bevande", "489", "", "", "", "", "3495038350", "nicolanisi@degiorgiobeverage.it", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Nicola Arnese", "490", "", "", "", "", "3200555306", "nicola695@libero.it", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Nicola dolci", "491", "", "", "", "", "3200555306", "nicola695@libero.it", "", "Italia", null));


        return suppliersList;
    }

    private List<ProductDTO> getPRoductListById(int id){

        List<ProductDTO> listProduct = new ArrayList<>();
        switch (id){
            case 463:
                listProduct.add(new ProductDTO(0, "Hamburger di manzo", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Hamburger ripieno", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Pollo croccante", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Tagliata di pollo", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Tagliata di manzo", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Tartare di manzo", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Tartare di tonno", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Polpo", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Stracciata di melanzane", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Polpette di carne", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Polpette di pane", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Pulled pork", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Cipolla caramellata", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Patate al forno", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Guacamole", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa poke 20m2", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa burger 20m2", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Maio sweet chili", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Maio teriaki", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Sauce cheddar", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Maio Japan", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Maio plic plac", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Maio rosmarino", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Bombette", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Focaccia", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Crema mascarpone", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Tiramisù", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Salame al cioccolato", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Cheesecake", "", UnitMeasure.KG, "", 0, 0, "", ""));
                break;
            case 464:
                listProduct.add(new ProductDTO(0, "Mozzarella", "", UnitMeasure.KG, "", 4, 6.9, "", ""));
                listProduct.add(new ProductDTO(0, "Stracciatella", "", UnitMeasure.UNITA, "", 10, 3.3, "", "8.10"));
                listProduct.add(new ProductDTO(0, "Bufala", "", UnitMeasure.KG, "", 10, 12.8, "", ""));
                listProduct.add(new ProductDTO(0, "Silano affumicato", "", UnitMeasure.KG, "", 4, 9.5, "", ""));
                listProduct.add(new ProductDTO(0, "Cacioricotta", "", UnitMeasure.UNITA, "", 0, 9.5, "", ""));
                listProduct.add(new ProductDTO(0, "Mozzarella affumicata", "", UnitMeasure.KG, "", 4, 6.9, "", ""));
                listProduct.add(new ProductDTO(0, "Ricotta", "", UnitMeasure.UNITA, "", 4, 2, "", ""));
                listProduct.add(new ProductDTO(0, "Burratina", "", UnitMeasure.UNITA, "", 10, 0, "", ""));
                break;
            case 466:
                listProduct.add(new ProductDTO(0, "Lattuga Iceberg", "", UnitMeasure.KG, "", 10, 2, "", ""));
                listProduct.add(new ProductDTO(0, "Tonno in Scatola", "", UnitMeasure.PACCHI, "", 10, 5.85, "", "a wx"));
                break;
            case 467:
                listProduct.add(new ProductDTO(0, "pane", "", UnitMeasure.KG, "", 4, 1, "", ""));
                listProduct.add(new ProductDTO(0, "ghiaccio", "", UnitMeasure.KG, "", 4, 0.75, "", ""));
                listProduct.add(new ProductDTO(0, "Soda", "", UnitMeasure.LITRI, "", 4, 1.75, "", ""));
                listProduct.add(new ProductDTO(0, "sale", "", UnitMeasure.KG, "", 5, 45.85, "", "acag"));
                break;
            case 468:
                listProduct.add(new ProductDTO(0, "Aperol", "", UnitMeasure.LITRI, "", 4, 11.58, "", ""));
                listProduct.add(new ProductDTO(0, "Gin", "", UnitMeasure.LITRI, "", 4, 12.85, "", ""));
                listProduct.add(new ProductDTO(0, "Peroni 33cl", "", UnitMeasure.BOTTIGLIA, "", 4, 0.25, "", ""));
                listProduct.add(new ProductDTO(0, "Prosecco doc", "", UnitMeasure.LITRI, "", 4, 12.52, "", ""));
                break;
            case 469:
                listProduct.add(new ProductDTO(0, "Select", "", UnitMeasure.LITRI, "", 0, 6.71, "", ""));
                listProduct.add(new ProductDTO(0, "Rosso antico", "", UnitMeasure.LITRI, "", 0, 6.5, "", ""));
                listProduct.add(new ProductDTO(0, "Belenkaya", "", UnitMeasure.LITRI, "", 0, 7.7, "", ""));
                listProduct.add(new ProductDTO(0, "Matusalem", "", UnitMeasure.LITRI, "", 0, 10.5, "", ""));
                listProduct.add(new ProductDTO(0, "Matusalem anejo", "", UnitMeasure.LITRI, "", 0, 12.6, "", ""));
                listProduct.add(new ProductDTO(0, "Beluga", "", UnitMeasure.LITRI, "", 0, 20.13, "", ""));
                listProduct.add(new ProductDTO(0, "Jose cuervo silver", "", UnitMeasure.LITRI, "", 0, 13.67, "", ""));
                listProduct.add(new ProductDTO(0, "Edgar", "", UnitMeasure.BOTTIGLIA, "", 0, 8.54, "", ""));
                listProduct.add(new ProductDTO(0, "Beluga 1,5", "", UnitMeasure.BOTTIGLIA, "", 22, 71.37, "", ""));
                listProduct.add(new ProductDTO(0, "Beluga 3 ", "", UnitMeasure.UNITA, "", 22, 150, "", ""));
                listProduct.add(new ProductDTO(0, "Beluga 6 ", "", UnitMeasure.UNITA, "", 22, 361, "", ""));
                listProduct.add(new ProductDTO(0, "Montenegro", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Dâ€™arapri", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Dâ€™arapri rose", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                break;
            case 470:
                listProduct.add(new ProductDTO(0, "Bombay dry", "", UnitMeasure.LITRI, "", 0, 10.5, "", ""));
                listProduct.add(new ProductDTO(0, "Bacardi blanco", "", UnitMeasure.LITRI, "", 0, 10.37, "", ""));
                listProduct.add(new ProductDTO(0, "Grey goose", "", UnitMeasure.LITRI, "", 0, 23.15, "", ""));
                listProduct.add(new ProductDTO(0, "Bombay saphire 70", "", UnitMeasure.LITRI, "", 0, 13.22, "", ""));
                listProduct.add(new ProductDTO(0, "Patron silver", "", UnitMeasure.LITRI, "", 0, 33, "", ""));
                listProduct.add(new ProductDTO(0, "Patron reposado", "", UnitMeasure.LITRI, "", 0, 39.72, "", ""));
                listProduct.add(new ProductDTO(0, "Patron anejo", "", UnitMeasure.LITRI, "", 0, 41.24, "", ""));
                listProduct.add(new ProductDTO(0, "Bacardi oro", "", UnitMeasure.LITRI, "", 0, 12.26, "", ""));
                break;
            case 471:
                listProduct.add(new ProductDTO(0, "Tonica 1 ", "", UnitMeasure.LITRI, "", 0, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Lemon 1", "", UnitMeasure.BOTTIGLIA, "", 0, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Keglevic pesca", "", UnitMeasure.BOTTIGLIA, "", 0, 9.64, "", ""));
                listProduct.add(new ProductDTO(0, "Prosecco", "", UnitMeasure.BOTTIGLIA, "", 0, 3.1, "", ""));
                listProduct.add(new ProductDTO(0, "Ginger beer fever", "", UnitMeasure.BOTTIGLIETTA, "", 0, 1.13, "", ""));
                listProduct.add(new ProductDTO(0, "Aperol", "", UnitMeasure.LITRI, "", 0, 11.6, "", ""));
                listProduct.add(new ProductDTO(0, "Triple sec bols", "", UnitMeasure.LITRI, "", 0, 9.64, "", ""));
                listProduct.add(new ProductDTO(0, "Passoa", "", UnitMeasure.BOTTIGLIA, "", 0, 11.9, "", ""));
                listProduct.add(new ProductDTO(0, "Red bull", "", UnitMeasure.ALTRO, "lattina", 0, 1.14, "", ""));
                listProduct.add(new ProductDTO(0, "Tonica vap", "", UnitMeasure.BOTTIGLIETTA, "", 0, 0.5, "", ""));
                listProduct.add(new ProductDTO(0, "Lemon vap", "", UnitMeasure.BOTTIGLIETTA, "", 0, 0.5, "", ""));
                listProduct.add(new ProductDTO(0, "Premix fragola", "", UnitMeasure.BOTTIGLIA, "", 0, 11, "", ""));
                listProduct.add(new ProductDTO(0, "Coca vap", "", UnitMeasure.BOTTIGLIETTA, "", 0, 0.56, "", ""));
                listProduct.add(new ProductDTO(0, "Zucchero liquido", "", UnitMeasure.BOTTIGLIA, "", 0, 5, "", ""));
                listProduct.add(new ProductDTO(0, "Succo ananas piccolo", "", UnitMeasure.BOTTIGLIETTA, "", 0, 0.62, "", ""));
                listProduct.add(new ProductDTO(0, "Succo arancia piccolo", "", UnitMeasure.BOTTIGLIETTA, "", 0, 0.62, "", ""));
                listProduct.add(new ProductDTO(0, "Midori", "", UnitMeasure.BOTTIGLIA, "", 0, 13, "", ""));
                listProduct.add(new ProductDTO(0, "Acqua fardello", "", UnitMeasure.FARDELLO, "", 0, 4, "", ""));
                listProduct.add(new ProductDTO(0, "Soda 2", "", UnitMeasure.BOTTIGLIA, "", 0, 4, "", ""));
                listProduct.add(new ProductDTO(0, "Acqua naturale fardello", "", UnitMeasure.FARDELLO, "", 0, 4, "", ""));
                listProduct.add(new ProductDTO(0, "Acqua frizzante fardello", "", UnitMeasure.FARDELLO, "", 0, 4, "", ""));
                listProduct.add(new ProductDTO(0, "Coca cola 1lt", "", UnitMeasure.BOTTIGLIA, "", 0, 1.1, "", ""));
                listProduct.add(new ProductDTO(0, "Belvedere", "", UnitMeasure.BOTTIGLIA, "", 0, 28.06, "", ""));
                listProduct.add(new ProductDTO(0, "Amaretto disaronno", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Ginger beer schewpps", "", UnitMeasure.BOTTIGLIA, "", 22, 0.5, "", ""));
                listProduct.add(new ProductDTO(0, "Mezcal verde", "", UnitMeasure.BOTTIGLIA, "", 22, 29.28, "", ""));
                listProduct.add(new ProductDTO(0, "Lemon soda lattina", "", UnitMeasure.UNITA, "", 22, 0.44, "", ""));
                listProduct.add(new ProductDTO(0, "Branca menta", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Antica formula", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Vermouth cocchi", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Vermouth madame", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Kinobi", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Cramberry succo", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Campari", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Nastro", "", UnitMeasure.CARTONI, "", 22, 18, "", ""));
                listProduct.add(new ProductDTO(0, "Peroni", "", UnitMeasure.CARTONI, "", 22, 13.5, "", ""));
                listProduct.add(new ProductDTO(0, "Tennets", "", UnitMeasure.CARTONI, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Ichnusa", "", UnitMeasure.CARTONI, "", 22, 18.91, "", ""));
                listProduct.add(new ProductDTO(0, "Montenegro", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Darapri", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                listProduct.add(new ProductDTO(0, "darapri rose", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Tonica fever", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Malibu", "", UnitMeasure.BOTTIGLIA, "", 22, 13, "", ""));
                listProduct.add(new ProductDTO(0, "Acqua frizzante grande", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                break;
            case 472:
                listProduct.add(new ProductDTO(0, "Hendricks 70", "", UnitMeasure.LITRI, "", 0, 25.9, "", ""));
                listProduct.add(new ProductDTO(0, "Sweet&sour", "", UnitMeasure.ALTRO, "bustina", 0, 3, "", ""));
                break;
            case 473:
                listProduct.add(new ProductDTO(0, "Bicchieri 350", "", UnitMeasure.UNITA, "", 0, 0.1, "", ""));
                break;
            case 474:
                listProduct.add(new ProductDTO(0, "Tovaglioli 25x25", "", UnitMeasure.PACCHI, "", 0, 2.17, "", ""));
                listProduct.add(new ProductDTO(0, "Buste nere 90x120", "", UnitMeasure.ALTRO, "cartone 20 KG", 0, 25.62, "", ""));
                listProduct.add(new ProductDTO(0, "Guanti s", "", UnitMeasure.UNITA, "", 0, 7.32, "", ""));
                listProduct.add(new ProductDTO(0, "Guanti m", "", UnitMeasure.UNITA, "", 0, 7.32, "", ""));
                listProduct.add(new ProductDTO(0, "Guanti L", "", UnitMeasure.UNITA, "", 0, 7.32, "", ""));
                listProduct.add(new ProductDTO(0, "Tovaglioli 24x24 12conf", "", UnitMeasure.CARTONI, "", 0, 26, "", ""));
                listProduct.add(new ProductDTO(0, "Tovaglioli 38x38 18conf", "", UnitMeasure.CARTONI, "", 0, 21.65, "", ""));
                listProduct.add(new ProductDTO(0, "Igenic floor 2 conf", "", UnitMeasure.CARTONI, "", 0, 11.72, "", ""));
                listProduct.add(new ProductDTO(0, "Carta formo", "", UnitMeasure.CARTONI, "", 0, 22.81, "", ""));
                listProduct.add(new ProductDTO(0, "Stovil power 15 lt", "", UnitMeasure.ALTRO, "bidone", 0, 39.65, "", ""));
                listProduct.add(new ProductDTO(0, "Rotolone 800str", "", UnitMeasure.ALTRO, "coppia", 0, 6.83, "", ""));
                listProduct.add(new ProductDTO(0, "Bicchieri 200cc ", "", UnitMeasure.UNITA, "", 4, 0.99, "", ""));
                listProduct.add(new ProductDTO(0, "Sgrassatore limone 750 ", "", UnitMeasure.UNITA, "", 22, 1.79, "", ""));
                listProduct.add(new ProductDTO(0, "Fornet 6UnitMeasure.KG", "", UnitMeasure.UNITA, "", 22, 16.16, "", ""));
                listProduct.add(new ProductDTO(0, "Cannucce h13 1000pz nere", "", UnitMeasure.UNITA, "", 22, 2.32, "", ""));
                listProduct.add(new ProductDTO(0, "Cannucce h21 nere", "", UnitMeasure.UNITA, "", 22, 6.34, "", ""));
                listProduct.add(new ProductDTO(0, "Fornet 750 ml", "", UnitMeasure.UNITA, "", 22, 2.93, "", ""));
                listProduct.add(new ProductDTO(0, "Spugne acciaio 25 pz", "", UnitMeasure.CARTONI, "", 22, 13, "", ""));
                listProduct.add(new ProductDTO(0, "Asciugamano 15 cnf", "", UnitMeasure.CARTONI, "", 22, 15.25, "", ""));
                listProduct.add(new ProductDTO(0, "Barchetta av 21,5x18x5", "", UnitMeasure.CARTONI, "", 22, 22.6, "", ""));
                listProduct.add(new ProductDTO(0, "Saniactive sgrassatore", "", UnitMeasure.UNITA, "", 22, 2.5, "", ""));
                listProduct.add(new ProductDTO(0, "Sapone liquido 3 lt", "", UnitMeasure.UNITA, "", 22, 4.76, "", ""));
                listProduct.add(new ProductDTO(0, "Spugna verde 10pz", "", UnitMeasure.CARTONI, "", 22, 5.86, "", ""));
                listProduct.add(new ProductDTO(0, "Panno blu vileda  10pz", "", UnitMeasure.CARTONI, "", 22, 3.48, "", ""));
                listProduct.add(new ProductDTO(0, "Spiedini 15 cm", "", UnitMeasure.CARTONI, "", 22, 6.22, "", ""));
                listProduct.add(new ProductDTO(0, "Spiedini 20cm", "", UnitMeasure.CARTONI, "", 22, 7.14, "", ""));
                listProduct.add(new ProductDTO(0, "Saccopoche 100pz", "", UnitMeasure.CARTONI, "", 22, 13.42, "", ""));
                listProduct.add(new ProductDTO(0, "Stuzzicadenti", "", UnitMeasure.CARTONI, "", 22, 3.9, "", ""));
                listProduct.add(new ProductDTO(0, "Acciaio lucidante", "", UnitMeasure.UNITA, "", 22, 5.67, "", ""));
                listProduct.add(new ProductDTO(0, "Sanialc", "", UnitMeasure.UNITA, "", 22, 1.9, "", ""));
                listProduct.add(new ProductDTO(0, "Buste goffrate 17x20 100pz", "", UnitMeasure.CARTONI, "", 22, 6.34, "", ""));
                listProduct.add(new ProductDTO(0, "Bicchiere 120cc", "", UnitMeasure.UNITA, "", 22, 1.88, "", ""));
                listProduct.add(new ProductDTO(0, "Brill power 15 lt", "", UnitMeasure.UNITA, "", 22, 42.09, "", ""));
                listProduct.add(new ProductDTO(0, "Panno giallo 5pz", "", UnitMeasure.CARTONI, "", 22, 9.76, "", ""));
                listProduct.add(new ProductDTO(0, "Bicchieri 25cc", "", UnitMeasure.UNITA, "", 22, 1.55, "", ""));
                listProduct.add(new ProductDTO(0, "Buste biocompostabili 90x120", "", UnitMeasure.CARTONI, "", 22, 33.37, "", ""));
                listProduct.add(new ProductDTO(0, "Candeggina 15 lt", "", UnitMeasure.CARTONI, "", 22, 6.22, "", ""));
                listProduct.add(new ProductDTO(0, "Shopper avano", "", UnitMeasure.UNITA, "", 22, 0.3, "", ""));
                listProduct.add(new ProductDTO(0, "Forchettine legno ", "", UnitMeasure.CARTONI, "", 22, 5.61, "", ""));
                listProduct.add(new ProductDTO(0, "Piatto asporto 50pz", "", UnitMeasure.UNITA, "", 22, 21.16, "", ""));
                listProduct.add(new ProductDTO(0, "Coperchio asporto ", "", UnitMeasure.UNITA, "", 22, 15.92, "", ""));
                listProduct.add(new ProductDTO(0, "Buste goffrate 20x30", "", UnitMeasure.CARTONI, "", 22, 9.58, "", ""));
                listProduct.add(new ProductDTO(0, "Buste ambra 42x28x85", "", UnitMeasure.CARTONI, "", 22, 42.7, "", ""));
                listProduct.add(new ProductDTO(0, "Rotolo alluminio 125mt", "", UnitMeasure.CARTONI, "", 22, 23.18, "", ""));
                listProduct.add(new ProductDTO(0, "Scatola hamburger 50pz", "", UnitMeasure.UNITA, "", 22, 11.47, "", ""));
                listProduct.add(new ProductDTO(0, "Barchetta av 20x12", "", UnitMeasure.UNITA, "", 22, 14.27, "", ""));
                listProduct.add(new ProductDTO(0, "Buste nere 90x120 20UnitMeasure.KG", "", UnitMeasure.CARTONI, "", 22, 25.62, "", ""));
                listProduct.add(new ProductDTO(0, "Rotolo pellicola 300 mt", "3 pz", UnitMeasure.CARTONI, "3 pz", 22, 17.87, "3 pz", ""));
                listProduct.add(new ProductDTO(0, "Geo piatti", "", UnitMeasure.UNITA, "", 22, 4.58, "", ""));
                break;
            case 475:
                listProduct.add(new ProductDTO(0, "Ca del bosco", "", UnitMeasure.LITRI, "", 0, 24.53, "", ""));
                listProduct.add(new ProductDTO(0, "Dompe luminor", "", UnitMeasure.LITRI, "", 0, 165, "", ""));
                listProduct.add(new ProductDTO(0, "Ghiaccio", "", UnitMeasure.CARTONI, "", 22, 7.2, "", ""));
                break;
            case 476:
                listProduct.add(new ProductDTO(0, "Lautant perrier", "", UnitMeasure.LITRI, "", 0, 32.15, "", ""));
                listProduct.add(new ProductDTO(0, "Laurant perrier rose", "", UnitMeasure.LITRI, "", 0, 54.3, "", ""));
                listProduct.add(new ProductDTO(0, "Calafuria", "", UnitMeasure.LITRI, "", 0, 8, "", ""));
                listProduct.add(new ProductDTO(0, "Chardonay", "", UnitMeasure.LITRI, "", 0, 5.26, "", ""));
                listProduct.add(new ProductDTO(0, "Fichimori", "", UnitMeasure.LITRI, "", 0, 6.47, "", ""));
                listProduct.add(new ProductDTO(0, "Perrier jouet", "", UnitMeasure.LITRI, "", 0, 42.5, "", ""));
                break;
            case 477:
                listProduct.add(new ProductDTO(0, "Tramari", "", UnitMeasure.LITRI, "", 0, 6.36, "", ""));
                listProduct.add(new ProductDTO(0, "Libol", "", UnitMeasure.LITRI, "", 0, 5.05, "", ""));
                listProduct.add(new ProductDTO(0, "Libel rose", "", UnitMeasure.LITRI, "", 0, 5.05, "", ""));
                listProduct.add(new ProductDTO(0, "Bellavista alma", "", UnitMeasure.LITRI, "", 0, 20.32, "", ""));
                break;
            case 478:
                listProduct.add(new ProductDTO(0, "Le rotaie", "", UnitMeasure.BOTTIGLIA, "", 0, 6.64, "", ""));
                listProduct.add(new ProductDTO(0, "Verso sud", "", UnitMeasure.BOTTIGLIA, "", 0, 6.64, "", ""));
                break;
            case 479:
                listProduct.add(new ProductDTO(0, "Baguette", "", UnitMeasure.UNITA, "", 0, 0.31, "", ""));
                listProduct.add(new ProductDTO(0, "Burger cereali", "", UnitMeasure.UNITA, "", 0, 0.34, "", ""));
                listProduct.add(new ProductDTO(0, " ", "", UnitMeasure.UNITA, "", 0, 0.28, "", ""));
                listProduct.add(new ProductDTO(0, "Panino hamburger", "", UnitMeasure.UNITA, "", 0, 0.43, "", ""));
                listProduct.add(new ProductDTO(0, "Club bianco", "", UnitMeasure.UNITA, "", 0, 0.33, "", ""));
                listProduct.add(new ProductDTO(0, "Club cereali", "", UnitMeasure.UNITA, "", 0, 0.38, "", ""));
                listProduct.add(new ProductDTO(0, "Taralli al primitivo", "", UnitMeasure.KG, "", 0, 7.2, "", ""));
                listProduct.add(new ProductDTO(0, "Pane casereccio", "", UnitMeasure.KG, "", 0, 2.4, "", ""));
                listProduct.add(new ProductDTO(0, "Panini hamburger mini", "", UnitMeasure.UNITA, "", 0, 0.18, "", ""));
                listProduct.add(new ProductDTO(0, "Frisa bianca", "", UnitMeasure.UNITA, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Frisa cereali", "", UnitMeasure.UNITA, "", 10, 0, "", ""));
                break;
            case 480:
                listProduct.add(new ProductDTO(0, "Hamburger vitello 180gr", "", UnitMeasure.UNITA, "", 0, 1.5, "", ""));
                listProduct.add(new ProductDTO(0, "Hamburger pollo 180gr", "", UnitMeasure.UNITA, "", 0, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Salsiccia sbudellata ", "", UnitMeasure.KG, "", 0, 7.9, "", ""));
                listProduct.add(new ProductDTO(0, "Tagliata di manzo", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Tartare di manzo ", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Filetto", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Mini hambuger", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Hamburger ripieno", "", UnitMeasure.PEZZI, "", 22, 0, "", ""));
                break;
            case 481:
                listProduct.add(new ProductDTO(0, "Controfiletto ", "", UnitMeasure.KG, "", 0, 8.14, "", ""));
                listProduct.add(new ProductDTO(0, "Petto intero", "", UnitMeasure.KG, "", 0, 8.64, "", ""));
                listProduct.add(new ProductDTO(0, "Petto senza osso", "", UnitMeasure.KG, "", 0, 9.25, "", ""));
                break;
            case 482:
                listProduct.add(new ProductDTO(0, "Capocollo", "", UnitMeasure.UNITA, "", 0, 35, "", ""));
                listProduct.add(new ProductDTO(0, "Filetto lardellato", "", UnitMeasure.UNITA, "", 0, 30, "", ""));
                listProduct.add(new ProductDTO(0, "Pancetta tesa", "", UnitMeasure.UNITA, "", 0, 30, "", ""));
                break;
            case 483:
                listProduct.add(new ProductDTO(0, "Formaggio cenere", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Formaggio nduja", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Formaggio finicchietto", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Caciocavallo ubriaco", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                break;
            case 484:
                listProduct.add(new ProductDTO(0, "Verdeca", "", UnitMeasure.BOTTIGLIA, "", 0, 4, "", ""));
                listProduct.add(new ProductDTO(0, "Nausica", "", UnitMeasure.BOTTIGLIA, "", 0, 5, "", ""));
                break;
            case 486:
                listProduct.add(new ProductDTO(0, "Clicquot", "", UnitMeasure.BOTTIGLIA, "", 0, 41.5, "", ""));
                listProduct.add(new ProductDTO(0, "Havana 7", "", UnitMeasure.BOTTIGLIA, "", 0, 25, "", ""));
                break;
            case 487:
                listProduct.add(new ProductDTO(0, "Rucola in mazzi", "", UnitMeasure.ALTRO, "mazzi", 0, 0.63, "", ""));
                listProduct.add(new ProductDTO(0, "Patate", "", UnitMeasure.KG, "", 0, 0.83, "", ""));
                listProduct.add(new ProductDTO(0, "Iceberg", "", UnitMeasure.UNITA, "", 0, 1.45, "", ""));
                listProduct.add(new ProductDTO(0, "Barbabietola rossa", "", UnitMeasure.UNITA, "", 0, 2.2, "", ""));
                listProduct.add(new ProductDTO(0, "Funghi champignon ", "", UnitMeasure.ALTRO, "vaschetta", 0, 1.3, "", ""));
                listProduct.add(new ProductDTO(0, "Mango", "", UnitMeasure.UNITA, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Zucchine", "", UnitMeasure.KG, "", 0, 1.14, "", ""));
                listProduct.add(new ProductDTO(0, "Cavolo cappuccio", "", UnitMeasure.UNITA, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Melanzane", "", UnitMeasure.KG, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Cipolla rossa", "", UnitMeasure.KG, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Noci sgusciate", "", UnitMeasure.KG, "", 0, 12.1, "", ""));
                listProduct.add(new ProductDTO(0, "Avocado", "", UnitMeasure.UNITA, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Limoni", "", UnitMeasure.KG, "", 0, 1.56, "", ""));
                listProduct.add(new ProductDTO(0, "Lime", "", UnitMeasure.KG, "", 0, 4.16, "", ""));
                listProduct.add(new ProductDTO(0, "Prezzemolo", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Finocchi", "", UnitMeasure.KG, "", 0, 1.24, "", ""));
                listProduct.add(new ProductDTO(0, "Pomodoro ramato", "", UnitMeasure.KG, "", 0, 1.56, "", ""));
                listProduct.add(new ProductDTO(0, "Datterino", "", UnitMeasure.KG, "", 0, 4.06, "", ""));
                listProduct.add(new ProductDTO(0, "Basilico", "", UnitMeasure.UNITA, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Timo", "", UnitMeasure.UNITA, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Granella pistacchio", "", UnitMeasure.KG, "", 0, 18.2, "", ""));
                listProduct.add(new ProductDTO(0, "Cetriolo", "", UnitMeasure.KG, "", 0, 1.56, "", ""));
                listProduct.add(new ProductDTO(0, "Salvia ", "", UnitMeasure.UNITA, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Carote", "", UnitMeasure.KG, "", 0, 1.04, "", ""));
                listProduct.add(new ProductDTO(0, "Arancia", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Lamelle di mandorle", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Funghi cardoncelli", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Menta box", "", UnitMeasure.CARTONI, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Passion fruit", "", UnitMeasure.CARTONI, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Anacardi", "", UnitMeasure.KG, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Sesamo nero", "", UnitMeasure.KG, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Barattiere", "", UnitMeasure.UNITA, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Rosmarino pianta", "", UnitMeasure.UNITA, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Peperoni", "", UnitMeasure.UNITA, "", 22, 0, "", ""));
                break;
            case 488:
                listProduct.add(new ProductDTO(0, "Tonno rosso", "", UnitMeasure.UNITA, "", 0, 36, "", ""));
                listProduct.add(new ProductDTO(0, "Polpo ", "", UnitMeasure.UNITA, "", 0, 50, "", ""));
                listProduct.add(new ProductDTO(0, "Salmone affumicato", "", UnitMeasure.UNITA, "", 0, 21.35, "", ""));
                break;
            case 489:
                listProduct.add(new ProductDTO(0, "Acqua piccola ", "", UnitMeasure.FARDELLO, "", 0, 5.17, "", ""));
                listProduct.add(new ProductDTO(0, "Fanta ", "", UnitMeasure.FARDELLO, "", 0, 14, "", ""));
                listProduct.add(new ProductDTO(0, "Fusto rossa bock", "", UnitMeasure.ALTRO, "fusto", 0, 93.8, "", ""));
                listProduct.add(new ProductDTO(0, "Bombola c02", "", UnitMeasure.BOTTIGLIA, "", 0, 12.81, "", ""));
                listProduct.add(new ProductDTO(0, "Coca cola vap", "", UnitMeasure.FARDELLO, "", 0, 14, "", ""));
                listProduct.add(new ProductDTO(0, "Coca cola zero vap", "", UnitMeasure.FARDELLO, "", 0, 14, "", ""));
                listProduct.add(new ProductDTO(0, "Corona ", "", UnitMeasure.CT, "", 0, 25, "", ""));
                listProduct.add(new ProductDTO(0, "Ichnusa", "", UnitMeasure.CT, "", 0, 19.43, "", ""));
                listProduct.add(new ProductDTO(0, "Lemon soda vap", "", UnitMeasure.FARDELLO, "", 0, 10.21, "", ""));
                listProduct.add(new ProductDTO(0, "Menabrea ", "", UnitMeasure.CT, "", 0, 21.81, "", ""));
                listProduct.add(new ProductDTO(0, "Nastroc", "", UnitMeasure.CT, "", 0, 17.73, "", ""));
                listProduct.add(new ProductDTO(0, "Fusto pils ", "", UnitMeasure.ALTRO, "FUSTO", 0, 105.6, "", ""));
                listProduct.add(new ProductDTO(0, "Prosecco", "", UnitMeasure.CT, "", 0, 20.72, "", ""));
                listProduct.add(new ProductDTO(0, "Tennets", "", UnitMeasure.CT, "", 0, 31.65, "", ""));
                listProduct.add(new ProductDTO(0, "Fusto weise", "", UnitMeasure.ALTRO, "FUSTO", 0, 99.24, "", ""));
                listProduct.add(new ProductDTO(0, "Aperol", "", UnitMeasure.BOTTIGLIA, "", 0, 11.82, "", ""));
                listProduct.add(new ProductDTO(0, "Grappa bianca", "", UnitMeasure.BOTTIGLIA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Grappa barricata", "", UnitMeasure.BOTTIGLIA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Gin mare", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Liquirizia", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Amaro del capo", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Don papa", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Bayles", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Tanqueray", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Limoncello", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Montenegro", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Jeghermaister", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Tonica vap", "", UnitMeasure.CARTONI, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Campari", "", UnitMeasure.BOTTIGLIA, "", 22, 15.92, "", ""));
                listProduct.add(new ProductDTO(0, "Bulldog", "", UnitMeasure.BOTTIGLIA, "", 22, 16.38, "", ""));
                listProduct.add(new ProductDTO(0, "Sky", "", UnitMeasure.BOTTIGLIA, "", 22, 11.63, "", ""));
                listProduct.add(new ProductDTO(0, "Tequila espolon blanco", "", UnitMeasure.BOTTIGLIA, "", 22, 17.64, "", ""));
                listProduct.add(new ProductDTO(0, "Bitter barbieri", "", UnitMeasure.BOTTIGLIA, "", 22, 9, "", ""));
                listProduct.add(new ProductDTO(0, "Cinzano rosso", "", UnitMeasure.BOTTIGLIA, "", 22, 8.49, "", ""));
                listProduct.add(new ProductDTO(0, "Red bull tonica", "", UnitMeasure.BOTTIGLIA, "", 22, 1.22, "", ""));
                listProduct.add(new ProductDTO(0, "Red bull Lemon", "", UnitMeasure.UNITA, "", 22, 1.22, "", ""));
                listProduct.add(new ProductDTO(0, "Limonata", "", UnitMeasure.CARTONI, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "The pesca", "", UnitMeasure.CARTONI, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "The limone", "", UnitMeasure.CARTONI, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Chinotto", "", UnitMeasure.CARTONI, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Cedrata", "", UnitMeasure.CARTONI, "", 22, 0, "", ""));
                break;
            case 490:
                listProduct.add(new ProductDTO(0, "Olive denocciolate", "", UnitMeasure.BARATTOLIPICCOLI, "", 0, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Conserva di peperoni piccanti", "", UnitMeasure.BARATTOLI, "", 0, 2.86, "", ""));
                listProduct.add(new ProductDTO(0, "Bacche vanilia", "", UnitMeasure.UNITA, "", 0, 5.06, "", ""));
                listProduct.add(new ProductDTO(0, "Formaggio fresco alpigiana", "", UnitMeasure.UNITA, "", 0, 5.15, "", ""));
                listProduct.add(new ProductDTO(0, "Taralli secchiello", "", UnitMeasure.SECCHIO, "", 0, 3.88, "", ""));
                listProduct.add(new ProductDTO(0, "Pepe nero", "", UnitMeasure.UNITA, "", 0, 7, "", ""));
                listProduct.add(new ProductDTO(0, "Farina caputo", "", UnitMeasure.UNITA, "", 0, 21.42, "", ""));
                listProduct.add(new ProductDTO(0, "Farina casillo ", "", UnitMeasure.UNITA, "", 0, 0.83, "", ""));
                listProduct.add(new ProductDTO(0, "Semola ", "", UnitMeasure.UNITA, "", 0, 0.99, "", ""));
                listProduct.add(new ProductDTO(0, "Tonno sott olio nostromo", "", UnitMeasure.UNITA, "", 0, 2, "", ""));
                listProduct.add(new ProductDTO(0, "Prosciutto cotto", "", UnitMeasure.UNITA, "", 0, 4.82, "", ""));
                listProduct.add(new ProductDTO(0, "Polpa di pomodoro", "", UnitMeasure.UNITA, "", 0, 7.69, "", ""));
                listProduct.add(new ProductDTO(0, "Olio extra vergine", "", UnitMeasure.UNITA, "", 0, 23.44, "", ""));
                listProduct.add(new ProductDTO(0, "Staccante spray", "", UnitMeasure.UNITA, "", 0, 5.61, "", ""));
                listProduct.add(new ProductDTO(0, "Olive nere 5UnitMeasure.KG", "", UnitMeasure.SECCHIO, "", 0, 4.11, "", ""));
                listProduct.add(new ProductDTO(0, "Passata di pomodoro divella", "", UnitMeasure.UNITA, "", 0, 0.64, "", ""));
                listProduct.add(new ProductDTO(0, "PatÃ¨ peperoncini", "", UnitMeasure.UNITA, "", 0, 5, "", ""));
                listProduct.add(new ProductDTO(0, "Mayonese bustine", "", UnitMeasure.CARTONI, "", 0, 7.7, "", ""));
                listProduct.add(new ProductDTO(0, "Pesto genovese", "", UnitMeasure.UNITA, "", 0, 9.15, "", ""));
                listProduct.add(new ProductDTO(0, "Ketchup bustine", "", UnitMeasure.CARTONI, "", 0, 5.8, "", ""));
                listProduct.add(new ProductDTO(0, "Mayonese 5UnitMeasure.KG", "", UnitMeasure.UNITA, "", 0, 12.77, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa tartara", "", UnitMeasure.BOTTIGLIA, "", 0, 5.49, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa bbq", "", UnitMeasure.BOTTIGLIA, "", 0, 4.5, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa pistacchio", "", UnitMeasure.BOTTIGLIA, "", 0, 14.54, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa boscaiola", "", UnitMeasure.BOTTIGLIA, "", 0, 6.22, "", ""));
                listProduct.add(new ProductDTO(0, "Mayonese brickl", "", UnitMeasure.BOTTIGLIA, "", 0, 6.22, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa bbq bustine", "", UnitMeasure.CARTONI, "", 0, 7.66, "", ""));
                listProduct.add(new ProductDTO(0, "Ketchup brick", "", UnitMeasure.BOTTIGLIA, "", 0, 2.75, "", ""));
                listProduct.add(new ProductDTO(0, "Senape brick", "", UnitMeasure.BOTTIGLIA, "", 0, 3.8, "", ""));
                listProduct.add(new ProductDTO(0, "Riso basmati 500gr", "", UnitMeasure.UNITA, "", 0, 2.07, "", ""));
                listProduct.add(new ProductDTO(0, "Riso venere 2UnitMeasure.KG", "", UnitMeasure.UNITA, "", 0, 7.49, "", ""));
                listProduct.add(new ProductDTO(0, "Orzo", "", UnitMeasure.UNITA, "", 0, 1.3, "", ""));
                listProduct.add(new ProductDTO(0, "Pan grattato  1UnitMeasure.KG", "", UnitMeasure.UNITA, "", 0, 1.5, "", ""));
                listProduct.add(new ProductDTO(0, "Prosciutto cotto altÃ  qualitÃ ", "", UnitMeasure.UNITA, "", 22, 7.86, "", ""));
                listProduct.add(new ProductDTO(0, "Petto arrosto", "", UnitMeasure.UNITA, "", 22, 9.5, "", ""));
                listProduct.add(new ProductDTO(0, "Panna oplÃ  500ml", "", UnitMeasure.UNITA, "", 22, 1.29, "", ""));
                listProduct.add(new ProductDTO(0, "Pomodori secchi", "", UnitMeasure.UNITA, "", 22, 10.56, "", ""));
                listProduct.add(new ProductDTO(0, "Funghi muschio", "", UnitMeasure.UNITA, "", 22, 15.85, "", ""));
                listProduct.add(new ProductDTO(0, "Kellogg,s corn flakes", "", UnitMeasure.UNITA, "", 22, 2.22, "", ""));
                listProduct.add(new ProductDTO(0, "Speck", "", UnitMeasure.UNITA, "", 22, 6.86, "", ""));
                listProduct.add(new ProductDTO(0, "Latte intero", "", UnitMeasure.UNITA, "", 22, 0.88, "", ""));
                listProduct.add(new ProductDTO(0, "Prosciutto Crudo di parma", "", UnitMeasure.UNITA, "", 22, 17.84, "", ""));
                listProduct.add(new ProductDTO(0, "Mortadella ", "", UnitMeasure.UNITA, "", 22, 9.5, "", ""));
                listProduct.add(new ProductDTO(0, "Salame spianata", "", UnitMeasure.UNITA, "", 22, 7.28, "", ""));
                listProduct.add(new ProductDTO(0, "Formaggio grattuggiato", "", UnitMeasure.UNITA, "", 22, 7.81, "", ""));
                listProduct.add(new ProductDTO(0, "Grana padano", "", UnitMeasure.UNITA, "", 22, 11.02, "", ""));
                listProduct.add(new ProductDTO(0, "Pan di stelle", "", UnitMeasure.UNITA, "", 22, 4.32, "", ""));
                listProduct.add(new ProductDTO(0, "Filetti di alici", "", UnitMeasure.UNITA, "", 10, 7.92, "", ""));
                listProduct.add(new ProductDTO(0, "Zucchero semolato", "", UnitMeasure.UNITA, "", 10, 0.95, "", ""));
                listProduct.add(new ProductDTO(0, "Nutella 3UnitMeasure.KG", "", UnitMeasure.UNITA, "", 10, 22.18, "", ""));
                listProduct.add(new ProductDTO(0, "Capperi", "", UnitMeasure.UNITA, "", 10, 4.22, "", ""));
                listProduct.add(new ProductDTO(0, "Livieti paneangeli", "", UnitMeasure.UNITA, "", 10, 2.95, "", ""));
                listProduct.add(new ProductDTO(0, "Pecorino", "", UnitMeasure.UNITA, "", 4, 8.88, "", ""));
                listProduct.add(new ProductDTO(0, "Olive leccino", "", UnitMeasure.UNITA, "", 10, 3.7, "", ""));
                listProduct.add(new ProductDTO(0, "Cacao amaro", "", UnitMeasure.UNITA, "", 10, 1.06, "", ""));
                listProduct.add(new ProductDTO(0, "Aceto di vino", "", UnitMeasure.UNITA, "", 10, 0.91, "", ""));
                listProduct.add(new ProductDTO(0, "Glassa ponti", "", UnitMeasure.UNITA, "", 10, 1.71, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa yogurt", "", UnitMeasure.UNITA, "", 10, 2.53, "", ""));
                listProduct.add(new ProductDTO(0, "Zucchero velo", "", UnitMeasure.UNITA, "", 10, 4.75, "", ""));
                listProduct.add(new ProductDTO(0, "Gelatina fogli", "", UnitMeasure.UNITA, "", 10, 0.95, "", ""));
                listProduct.add(new ProductDTO(0, "Crema tartufata", "", UnitMeasure.UNITA, "", 10, 11.62, "", ""));
                listProduct.add(new ProductDTO(0, "Cioccolato bianco", "", UnitMeasure.UNITA, "", 10, 14.26, "", ""));
                listProduct.add(new ProductDTO(0, "Formaggio quick", "", UnitMeasure.UNITA, "", 10, 0.59, "", ""));
                listProduct.add(new ProductDTO(0, "Olio di semi di girasoli 5lt", "", UnitMeasure.UNITA, "", 10, 17.42, "", ""));
                listProduct.add(new ProductDTO(0, "Rodez", "", UnitMeasure.UNITA, "", 4, 10.52, "", ""));
                listProduct.add(new ProductDTO(0, "Oro saiva", "", UnitMeasure.UNITA, "", 10, 3.64, "", ""));
                listProduct.add(new ProductDTO(0, "Sale grosso", "", UnitMeasure.UNITA, "", 22, 0.31, "", ""));
                listProduct.add(new ProductDTO(0, "Sale fino", "", UnitMeasure.UNITA, "", 22, 0.3, "", ""));
                listProduct.add(new ProductDTO(0, "Pancetta stufata", "", UnitMeasure.UNITA, "", 10, 5.7, "", ""));
                listProduct.add(new ProductDTO(0, "Frutti di bosco marmellata", "", UnitMeasure.UNITA, "", 10, 1.69, "", ""));
                listProduct.add(new ProductDTO(0, "Savoiardi", "", UnitMeasure.UNITA, "", 10, 1.37, "", ""));
                listProduct.add(new ProductDTO(0, "Wustel", "", UnitMeasure.UNITA, "", 10, 1.37, "", ""));
                listProduct.add(new ProductDTO(0, "Panna da cucina", "", UnitMeasure.UNITA, "", 10, 0.71, "", ""));
                listProduct.add(new ProductDTO(0, "Cioccolato fondente", "", UnitMeasure.UNITA, "", 10, 3.06, "", ""));
                listProduct.add(new ProductDTO(0, "Patate fry ", "", UnitMeasure.UNITA, "", 4, 2.45, "", ""));
                listProduct.add(new ProductDTO(0, "Cime di rapa", "", UnitMeasure.UNITA, "", 4, 4.19, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa di soya", "", UnitMeasure.UNITA, "", 10, 2.85, "", ""));
                listProduct.add(new ProductDTO(0, "Topping caramello", "", UnitMeasure.UNITA, "", 10, 9.5, "", ""));
                listProduct.add(new ProductDTO(0, "Filetti di alici ", "", UnitMeasure.UNITA, "", 10, 7.66, "", ""));
                listProduct.add(new ProductDTO(0, "Pancetta scotennata", "", UnitMeasure.UNITA, "", 10, 8.87, "", ""));
                listProduct.add(new ProductDTO(0, "Gorgonzola", "", UnitMeasure.UNITA, "", 4, 7.44, "", ""));
                listProduct.add(new ProductDTO(0, "Burro 1UnitMeasure.KG", "", UnitMeasure.UNITA, "", 4, 7.6, "", ""));
                listProduct.add(new ProductDTO(0, "Mascarpone 250gr", "", UnitMeasure.UNITA, "", 4, 1.6, "", ""));
                listProduct.add(new ProductDTO(0, "Cheddar", "", UnitMeasure.UNITA, "", 4, 7.68, "", ""));
                listProduct.add(new ProductDTO(0, "Bresaola", "", UnitMeasure.KG, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Soppressata piccante", "", UnitMeasure.KG, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Bacon", "", UnitMeasure.UNITA, "", 22, 0, "", ""));
                break;
            case 491:
                listProduct.add(new ProductDTO(0, "Pistacchiello 12pz", "", UnitMeasure.CARTONI, "", 10, 22.57, "", ""));
                listProduct.add(new ProductDTO(0, "Tiramisu 12pz", "", UnitMeasure.CARTONI, "", 10, 27.72, "", ""));
                listProduct.add(new ProductDTO(0, "Cheesecake mirtilli 6pz", "", UnitMeasure.CARTONI, "", 10, 13.86, "", ""));
                listProduct.add(new ProductDTO(0, "Cheecake cioccolato 6pz", "", UnitMeasure.CARTONI, "", 10, 13.86, "", ""));
                listProduct.add(new ProductDTO(0, "Salame cacao", "", UnitMeasure.PEZZI, "", 10, 16.37, "", ""));
                listProduct.add(new ProductDTO(0, "Barattolo tre cioccolati", "", UnitMeasure.CARTONI, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Barattolo chantilly", "", UnitMeasure.CARTONI, "", 10, 0, "", ""));
                break;

        }
        return listProduct;
    }

}
