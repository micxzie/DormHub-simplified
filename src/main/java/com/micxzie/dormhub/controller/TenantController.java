package com.micxzie.dormhub.controller;

import com.micxzie.dormhub.model.Tenant;
import com.micxzie.dormhub.repository.TenantRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantRepository tenantRepository;

    // Spring automatically provides (injects) the repository here — 
    // you never write "new TenantRepository()" yourself.
    public TenantController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @GetMapping
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }
}