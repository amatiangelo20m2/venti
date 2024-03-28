package com.ventimetriconsulting.branch.configuration.bookingconf.controller;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.booking.Customer;
import com.ventimetriconsulting.branch.configuration.bookingconf.entity.booking.dto.BookingDTO;
import com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto.*;
import com.ventimetriconsulting.branch.configuration.bookingconf.service.BookingService;
import com.ventimetriconsulting.branch.exception.customexceptions.GlobalException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping(path="/createbranchconfiguration")
    public ResponseEntity<BranchConfigurationDTO> createBranchConfiguration(@RequestParam String branchCode){
        BranchConfigurationDTO branchConfigurationDTO = bookingService.configureBranchWithDefaultOptions(branchCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(branchConfigurationDTO);
    }

    @GetMapping(path = "/configuration/waapi/instance/configure")
    public ResponseEntity<BranchConfigurationDTO> configureNumberForWhatsAppMessaging(@RequestParam String branchCode){
        BranchConfigurationDTO branchConfigurationDTO = bookingService.configureNumberForWhatsAppMessaging(branchCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(branchConfigurationDTO);

    }

    @GetMapping(path = "/configuration/waapi/instance/checkstatus")
    @ResponseStatus(HttpStatus.OK)
    public BranchConfigurationDTO checkWaApiStatus(@RequestParam String branchCode){
        return bookingService.checkWaApiStatus(branchCode);
    }

    @GetMapping(path = "/configuration/waapi/instance/reboot")
    @ResponseStatus(HttpStatus.OK)
    public BranchConfigurationDTO reboot(@RequestParam String branchCode){
        return bookingService.reboot(branchCode);
    }

    @PostMapping(path = "/configuration/timerange/update")
    @ResponseStatus(HttpStatus.OK)
    public BranchConfigurationDTO updateTimeRange(
            @RequestBody UpdateBranchTimeRanges updateBranchTimeRanges){
        return bookingService.updateTimeRangeConfiguration(updateBranchTimeRanges);
    }

    @DeleteMapping(path = "/deletetimerange")
    @ResponseStatus(HttpStatus.OK)
    public BranchConfigurationDTO deleteTimeRange(@RequestParam long timeRangeId){
        return bookingService.deleteTimeRange(timeRangeId);
    }

    @PostMapping(path = "/updateconfiguration")
    @ResponseStatus(HttpStatus.OK)
    public BranchConfigurationDTO updateConfiguration(@RequestBody BranchGeneralConfigurationEditRequest branchGeneralConfigurationEditRequest){
        return bookingService.updateConfiguration(branchGeneralConfigurationEditRequest);
    }

    @PostMapping(path = "/booking/create")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createBooking(@RequestBody CreateBookingRequest createBookingRequest){
        bookingService.createBooking(createBookingRequest);
    }

    @GetMapping(path = "/register/customer")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer registerCustomer(@RequestParam String brancCode,
                                     @RequestParam String name,
                                     @RequestParam String lastname,
                                     @RequestParam String email,
                                     @RequestParam String prefix,
                                     @RequestParam String phone,
                                     @RequestParam LocalDate dob,
                                     @RequestParam boolean treatmentPersonalData,
                                     @RequestParam String photoUrl){

        return bookingService.registerCustomer(
                brancCode,
                name,
                lastname,
                email,
                prefix,
                phone,
                dob,
                treatmentPersonalData,
                photoUrl);

    }

//    @PostMapping(path = "/create/tag")
//    @ResponseStatus(HttpStatus.OK)
//    public FormTag createTag(@RequestParam String tagName, @RequestParam String branchCode){
//        return bookingService.createTag(tagName, branchCode);
//    }

//    @PostMapping(path = "/delete/tag")
//    @ResponseStatus(HttpStatus.OK)
//    public void deleteTag(@RequestParam String tagName, @RequestParam String branchCode){
//        bookingService.deleteTag(tagName, branchCode);
//    }

    @GetMapping(path = "/retrieveformdata")
    @ResponseStatus(HttpStatus.OK)
    public CustomerFormData retrieveFormData(@RequestParam String branchCode,
                                             @RequestParam String formCode){

        return bookingService.retrieveFormData(branchCode, formCode);

    }


    @GetMapping(path = "/switchisclosedbranchtime")
    @ResponseStatus(HttpStatus.OK)
    public void switchIsClosedBranchTime(@RequestParam Long branchTimeRangeId){
        bookingService.switchIsClosedValueToBranchTimeRange(branchTimeRangeId);
    }

    @GetMapping(path = "/retrievecustomerandsendotp")
    @ResponseStatus(HttpStatus.OK)
    public CustomerResult retrieveCustomerAndSendOtp(@RequestParam String branchCode,
                                                     @RequestParam String prefix,
                                                     @RequestParam String phone) {

        return bookingService.retrieveCustomerByPrefixPhoneAndSendOtp(branchCode, prefix, phone);
    }

    @GetMapping(path = "/retrievecustomer")
    @ResponseStatus(HttpStatus.OK)
    public CustomerResult retrieveCustomer(
            @RequestParam String prefix,
            @RequestParam String phone) {

        return bookingService.retrieveCustomerByPrefixPhone(prefix, phone);
    }

    @GetMapping(path = "booking/getlist")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDTO> retrieveBookingsByBranchCode(@RequestParam String branchCode,
                                                         @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                         @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return bookingService.retrieveBookingsByBranchCode(branchCode, startDate, endDate);
    }

//    @GetMapping(path = "/sendopt")
//    @ResponseStatus(HttpStatus.OK)
//    public String sendOtp(@RequestParam String phone,
//                          @RequestParam String branchCode){
//
//        return bookingService.sendOtp(phone, branchCode);
//
//    }

    //TODO: check this part for future integration of massive sending message. Righ now is not working while 'chatId' format wrong

//    @GetMapping(path = "/sendmassivemessage")
//    @ResponseStatus(HttpStatus.OK)
//    public void sendMassiveMessage(){
//        String message = "Giovedì sera si accendono le luci a Monopoli!  \uD83C\uDFB8\uD83C\uDFA4Preparati a vivere l'emozione del rock puro con la cover band più esplosiva dei Guns N' Rose. Rivivi i classici che hanno fatto la storia del rock in una serata indimenticabile.  \uD83D\uDD25 Lasciati travolgere da hit come \\\" Sweet Child O ' Mine\\\" e \\\"November Rain\\\" in una location mozzafiato!  \uD83D\uDCCD Dove? Monopoli, il cuore pulsante del rock. ⏰ Quando? Giovedì sera - L' esperienza rock inizia quando cala il sole!La Cover Band dei Guns n’ Roses ti aspetta per una notte di pura adrenalina rock!PRENOTA IL TUO TAVOLO: https: //20m2official.it/bookingmonopoli";
//        String mediaUrl = "http://20m2official.it/wp-content/uploads/2024/01/WhatsApp-Image-2024-01-16-at-17.01.42.jpeg";
//        String imageName = "gunsroses";
//        String instaanceId = "4629";
//
//        List<String> list = new ArrayList<>();
//        list.add("393454937047@c.us");
//
//        waApiService.sendMessageWithImage(instaanceId, list , mediaUrl, imageName, message);
//    }


}
