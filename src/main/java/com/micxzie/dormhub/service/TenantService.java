package com.micxzie.dormhub.service;

import com.micxzie.dormhub.exception.DuplicateResourceException;
import com.micxzie.dormhub.exception.InvalidRequestException;
import com.micxzie.dormhub.exception.ResourceNotFoundException;
import com.micxzie.dormhub.model.Tenant;
import com.micxzie.dormhub.repository.TenantRepository;
import com.micxzie.dormhub.security.SecurityUtil;
import com.micxzie.dormhub.repository.LeaseRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final LeaseRepository leaseRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;

    // Same pattern as before — Spring injects the repository automatically.
    public TenantService(TenantRepository tenantRepository, PasswordEncoder passwordEncoder, 
        LeaseRepository leaseRepository, SecurityUtil securityUtil) {
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
        this.leaseRepository = leaseRepository;
        this.securityUtil = securityUtil;
    }

    public Tenant authenticate(String email, String rawPassword) {
        Tenant tenant = getTenantByEmail(email);

        if (!passwordEncoder.matches(rawPassword, tenant.getPasswordHash())) {
            throw new InvalidRequestException("Invalid email or password");
        }

        return tenant;
    }

    public List<Tenant> getAllTenants() {
        if (securityUtil.isCurrentUserAdmin()) {
        return tenantRepository.findAll();
        }

        Tenant currentTenant = getTenantByEmail(securityUtil.getCurrentUserEmail());
        return List.of(currentTenant);
    }

    public Tenant getTenantById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));

        if (!securityUtil.isCurrentUserAdmin() && !tenant.getEmail().equals(securityUtil.getCurrentUserEmail())) {
            throw new InvalidRequestException("You are not authorized to view this tenant's data");
        }

        return tenant;
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
        Tenant existingTenant = getTenantById(id); // the tenant actually being edited

        if (!securityUtil.isCurrentUserAdmin() && !existingTenant.getEmail().equals(securityUtil.getCurrentUserEmail())) {
            throw new InvalidRequestException("You are not authorized to update this tenant's data");
        }

        // Only an admin is allowed to change a tenant's role
        if (!updatedTenant.getRole().equals(existingTenant.getRole()) && !securityUtil.isCurrentUserAdmin()) {
            throw new InvalidRequestException("Only an admin can change a tenant's role");
        }

        existingTenant.setFirstName(updatedTenant.getFirstName());
        existingTenant.setLastName(updatedTenant.getLastName());
        existingTenant.setEmail(updatedTenant.getEmail());
        existingTenant.setPhone(updatedTenant.getPhone());
        existingTenant.setRole(updatedTenant.getRole());

        return tenantRepository.save(existingTenant);
    }

    public void deleteTenant(Long id) {
        Tenant existingTenant = getTenantById(id); // throws if not found, so we don't delete nothing
        
        if (leaseRepository.existsByTenant(existingTenant)) {
        throw new InvalidRequestException("Cannot delete tenant with existing lease records");
        }

        tenantRepository.delete(existingTenant);
    }
}