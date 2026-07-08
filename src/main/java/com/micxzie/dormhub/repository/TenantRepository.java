package com.micxzie.dormhub.repository;

import com.micxzie.dormhub.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    // Empty for now — and that's genuinely fine.
}