package com.micxzie.dormhub.service;

import com.micxzie.dormhub.exception.InvalidRequestException;
import com.micxzie.dormhub.exception.ResourceNotFoundException;
import com.micxzie.dormhub.model.Lease;
import com.micxzie.dormhub.model.Room;
import com.micxzie.dormhub.model.Tenant;
import com.micxzie.dormhub.repository.LeaseRepository;
import com.micxzie.dormhub.repository.RoomRepository;
import com.micxzie.dormhub.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaseService {

    private final LeaseRepository leaseRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    public LeaseService(LeaseRepository leaseRepository,
                         RoomRepository roomRepository,
                         TenantRepository tenantRepository) {
        this.leaseRepository = leaseRepository;
        this.roomRepository = roomRepository;
        this.tenantRepository = tenantRepository;
    }

    public List<Lease> getAllLeases() {
        return leaseRepository.findAll();
    }

    public Lease getLeaseById(Long id) {
        return leaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lease not found with id: " + id));
    }

    public Lease createLease(Lease lease) {
        // Step 1: Confirm the tenant actually exists
        Tenant tenant = tenantRepository.findById(lease.getTenant().getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tenant not found with id: " + lease.getTenant().getTenantId()));

        // Step 2: Confirm the room actually exists
        Room room = roomRepository.findById(lease.getRoom().getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with id: " + lease.getRoom().getRoomId()));

        // Step 3: Business rule — room can't exceed its capacity in active leases
        long activeLeaseCount = leaseRepository.countByRoomAndStatus(room, "ACTIVE");
        if (activeLeaseCount >= room.getCapacity()) {
            throw new InvalidRequestException("Room is at full capacity, no more active leases allowed");
        }

        // Step 4: Attach the real, verified objects before saving
        lease.setTenant(tenant);
        lease.setRoom(room);

        return leaseRepository.save(lease);
    }

    public void deleteLease(Long id) {
        Lease existingLease = getLeaseById(id);
        leaseRepository.delete(existingLease);
    }
}