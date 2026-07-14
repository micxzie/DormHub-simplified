package com.micxzie.dormhub.controller;

import com.micxzie.dormhub.dto.LoginRequest;
import com.micxzie.dormhub.dto.LoginResponse;
import com.micxzie.dormhub.model.Tenant;
import com.micxzie.dormhub.security.JwtUtil;
import com.micxzie.dormhub.service.TenantService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final TenantService tenantService;
    private final JwtUtil jwtUtil;

    public AuthController(TenantService tenantService, JwtUtil jwtUtil) {
        this.tenantService = tenantService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public Tenant register(@RequestBody Tenant tenant) {
        return tenantService.createTenant(tenant);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        Tenant tenant = tenantService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        String token = jwtUtil.generateToken(tenant.getEmail(), tenant.getRole());
        return new LoginResponse(token, tenant.getEmail(), tenant.getRole());
    }
}