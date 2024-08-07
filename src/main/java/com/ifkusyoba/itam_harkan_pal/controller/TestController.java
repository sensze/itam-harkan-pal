package com.ifkusyoba.itam_harkan_pal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/")
    public String helloWorld() {
        return "Welcome to ITAM Harkan PT. PAL Indonesia Endpoint";
    }
}
