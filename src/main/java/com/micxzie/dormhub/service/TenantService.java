package com.micxzie.dormhub.service;

import com.micxzie.dormhub.exception.DuplicateResourceException;
import com.micxzie.dormhub.exception.InvalidRequestException;
import com.micxzie.dormhub.exception.ResourceNotFoundException;
import com.micxzie.dormhub.model.Tenant;
import com.micxzie.dormhub.repository.TenantRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    // Same pattern as before — Spring injects the repository automatically.
    public TenantService(TenantRepository tenantRepository, PasswordEncoder passwordEncoder) {
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Tenant authenticate(String email, String rawPassword) {
        Tenant tenant = getTenantByEmail(email);

        if (!passwordEncoder.matches(rawPassword, tenant.getPasswordHash())) {
            throw new InvalidRequestException("Invalid email or password");
        }

        return tenant;
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Tenant getTenantById(Long id) {
    return tenantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
    }

    public Tenant getTenantByEmail(String email) {
    return tenantRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + email));
    }

    public Tenant createTenant(Tenant tenant) {
        if (tenantRepository.findByEmail(tenant.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already in use: " + tenant.getEmail());
        }

        tenant.setPasswordHash(passwordEncoder.encode(tenant.getPasswordHash()));
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