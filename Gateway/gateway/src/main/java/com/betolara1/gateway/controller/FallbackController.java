package com.betolara1.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @org.springframework.web.bind.annotation.RequestMapping("/fallback")
    public ResponseEntity<String> fallback() {
        return ResponseEntity.status(503)
                .body("⚠️ Serviço temporariamente indisponível. Por favor, tente novamente mais tarde.");
    }
}
