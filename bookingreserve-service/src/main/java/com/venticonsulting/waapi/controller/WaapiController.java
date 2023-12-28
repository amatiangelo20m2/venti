package com.venticonsulting.waapi.controller;

import com.venticonsulting.waapi.entity.dto.WaApiConfigDTO;
import com.venticonsulting.waapi.service.WhatsAppApiService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/waapi")
public class WaapiController {

    private final WhatsAppApiService whatsAppApiService;

    public WaapiController(WhatsAppApiService whatsAppApiService) {
        this.whatsAppApiService = whatsAppApiService;
    }

    @GetMapping(path = "/instance/configure")
    @ResponseStatus(HttpStatus.CREATED)
    public WaApiConfigDTO configureNumberForWhatsAppMessaging(@RequestParam String branchCode){
        return whatsAppApiService.configureNumberForWhatsAppMessaging(branchCode);
    }

    @GetMapping(path = "/instance/checkstatus")
    @ResponseStatus(HttpStatus.OK)
    public WaApiConfigDTO checkWaApiStatus(@RequestParam String branchCode){
        return whatsAppApiService.checkWaApiStatus(branchCode);
    }

    @GetMapping(path = "/instance/reboot")
    @ResponseStatus(HttpStatus.OK)
    public WaApiConfigDTO reboot(@RequestParam String branchCode){
        return whatsAppApiService.reboot(branchCode);
    }

//
//    @GetMapping(path = "/instance/retrieveqr")
//    @ResponseStatus(HttpStatus.OK)
//    public void retrieveQrCode(@RequestParam String instanceId){
//        whatsAppApiService.retrieveQrCode(instanceId);
//    }

}
