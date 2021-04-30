package com.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wzyang
 */
@RestController
@RefreshScope
public class BusConfigController {

    @Value("${config.info}")
    private String value;


    @GetMapping(value = "/echo")
    public String echo(){
        return value;
    }


}
