package com.venticonsulting.waapi.controller;

import com.venticonsulting.waapi.entity.dto.RestaurantConfigurationDTO;
import com.venticonsulting.waapi.entity.dto.UpdateRestaurantConfigurationRequest;
import com.venticonsulting.waapi.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping(path = "/configuration/waapi/instance/configure")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantConfigurationDTO configureNumberForWhatsAppMessaging(@RequestParam String branchCode){
        return bookingService.configureNumberForWhatsAppMessaging(branchCode);
    }

    @GetMapping(path = "/configuration/waapi/instance/checkstatus")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantConfigurationDTO checkWaApiStatus(@RequestParam String branchCode){
        return bookingService.checkWaApiStatus(branchCode);
    }

    @GetMapping(path = "/configuration/waapi/instance/reboot")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantConfigurationDTO reboot(@RequestParam String branchCode){
        return bookingService.reboot(branchCode);
    }

    @PostMapping(path = "/configuration/timerange/update")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantConfigurationDTO updateTimeRange(
            @RequestBody UpdateRestaurantConfigurationRequest updateRestaurantConfigurationRequest){
        return bookingService.updateTimeRangeConfiguration(updateRestaurantConfigurationRequest);
    }

}
