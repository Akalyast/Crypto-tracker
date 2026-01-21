package com.CryptoProject.CryptoInfosys.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CryptoProject.CryptoInfosys.dto.TaxSummaryDTO;
import com.CryptoProject.CryptoInfosys.model.User;
import com.CryptoProject.CryptoInfosys.repository.UserRepository;
import com.CryptoProject.CryptoInfosys.security.JwtUtils;
import com.CryptoProject.CryptoInfosys.service.TaxService;

@RestController
@RequestMapping("/tax")
@CrossOrigin(origins = "http://localhost:3000")
public class TaxController {

    private final TaxService taxService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepo;

    public TaxController(
            TaxService taxService,
            JwtUtils jwtUtils,
            UserRepository userRepo) {
        this.taxService = taxService;
        this.jwtUtils = jwtUtils;
        this.userRepo = userRepo;
    }

    @GetMapping("/hints")
    public TaxSummaryDTO getTaxHints(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtUtils.extractUsername(token);

        User user = userRepo.findByEmail(email).orElseThrow();

        return taxService.calculateTaxHints(user.getId());
    }
}
