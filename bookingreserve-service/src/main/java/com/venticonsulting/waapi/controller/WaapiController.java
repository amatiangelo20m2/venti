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

    @DeleteMapping(path = "/instance/delete")
    @ResponseStatus(HttpStatus.OK)
    public void retrieveUserById(@RequestParam String instanceId){
        whatsAppApiService.deleteInstance(instanceId);
    }

//    @GetMapping(path = "/instance/retrieve/status")
//    @ResponseStatus(HttpStatus.OK)
//    public void retrieveIntanceStatus(@RequestParam String instanceId){
//        whatsAppApiService.retrieveIntanceStatus(instanceId);
//    }

//    @GetMapping(path = "/instance/retrieve/clientinfo")
//    @ResponseStatus(HttpStatus.OK)
//    public void retrieveClientInfo(@RequestParam String instanceId){
//        whatsAppApiService.retrieveClientInfo(instanceId);
//    }
//
//    @GetMapping(path = "/instance/retrieveqr")
//    @ResponseStatus(HttpStatus.OK)
//    public void retrieveQrCode(@RequestParam String instanceId){
//        whatsAppApiService.retrieveQrCode(instanceId);
//    }

}
