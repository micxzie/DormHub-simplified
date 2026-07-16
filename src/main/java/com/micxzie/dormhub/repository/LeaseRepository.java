package com.micxzie.dormhub.repository;

import com.micxzie.dormhub.model.Lease;
import com.micxzie.dormhub.model.Room;
import com.micxzie.dormhub.model.Tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long>{
    long countByRoomAndStatus(Room room, String status);
    boolean existsByTenant(Tenant tenant);
}