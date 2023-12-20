package com.venticonsulting.bookingservice.controller;

import com.venticonsulting.bookingservice.entity.dto.CustomerResponseEntity;
import com.venticonsulting.bookingservice.service.WhatsAppApiService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/waapi")
@AllArgsConstructor
public class WaapiController {

    private WhatsAppApiService whatsAppApiService;

    @GetMapping(path = "/instance/create")
    @ResponseStatus(HttpStatus.OK)
    public void retrieveUserById(){
        whatsAppApiService.createInstance();
    }

    @DeleteMapping(path = "/instance/delete")
    @ResponseStatus(HttpStatus.OK)
    public void retrieveUserById(@RequestParam String instanceId){
        whatsAppApiService.deleteInstance(instanceId);
    }

    @GetMapping(path = "/instance/retrieve/status")
    @ResponseStatus(HttpStatus.OK)
    public void retrieveIntanceStatus(@RequestParam String instanceId){
        whatsAppApiService.retrieveIntanceStatus(instanceId);
    }

    @GetMapping(path = "/instance/retrieve/clientinfo")
    @ResponseStatus(HttpStatus.OK)
    public void retrieveClientInfo(@RequestParam String instanceId){
        whatsAppApiService.retrieveClientInfo(instanceId);
    }

    @GetMapping(path = "/instance/retrieveqr")
    @ResponseStatus(HttpStatus.OK)
    public void retrieveQrCode(@RequestParam String instanceId){
        whatsAppApiService.retrieveQrCode(instanceId);
    }

}
