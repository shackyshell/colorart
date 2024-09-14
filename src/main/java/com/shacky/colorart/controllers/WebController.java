package com.shacky.colorart.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @RequestMapping("/")
    public String home() {
        // Ensure this returns the view for static content
        return "index"; // This maps to src/main/resources/templates/index.html if using Thymeleaf
    }
}