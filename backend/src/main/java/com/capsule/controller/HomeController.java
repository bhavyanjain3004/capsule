package com.capsule.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
            "name", "Capsule API",
            "version", "0.0.1",
            "status", "UP",
            "message", "Welcome to Capsule - Seal a Memory in Time."
        );
    }
}
