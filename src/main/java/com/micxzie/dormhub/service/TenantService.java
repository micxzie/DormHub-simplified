package com.micxzie.dormhub.service;

import com.micxzie.dormhub.model.Tenant;
import com.micxzie.dormhub.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    // Same pattern as before — Spring injects the repository automatically.
    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Tenant getTenantById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + id));
    }

    public Tenant createTenant(Tenant tenant) {
        // Business rule: no two tenants can share an email.
        if (tenantRepository.findByEmail(tenant.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use: " + tenant.getEmail());
        }
        return tenantRepository.save(tenant);
    }

    public Tenant updateTenant(Long id, Tenant updatedTenant) {
    Tenant existingTenant = getTenantById(id); // reuses the method we already wrote

    existingTenant.setFirstName(updatedTenant.getFirstName());
    existingTenant.setLastName(updatedTenant.getLastName());
    existingTenant.setEmail(updatedTenant.getEmail());
    existingTenant.setPhone(updatedTenant.getPhone());
    existingTenant.setRole(updatedTenant.getRole());

    return tenantRepository.save(existingTenant);
    }

    public void deleteTenant(Long id) {
        Tenant existingTenant = getTenantById(id); // throws if not found, so we don't delete nothing
        tenantRepository.delete(existingTenant);
    }
}