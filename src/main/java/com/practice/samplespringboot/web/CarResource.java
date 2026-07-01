package com.practice.samplespringboot.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarResource {

    @GetMapping(value = "/test")
    public String test(){
        return "Test";
    }

}
