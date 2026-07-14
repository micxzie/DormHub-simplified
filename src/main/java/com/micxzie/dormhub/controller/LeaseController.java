package com.micxzie.dormhub.controller;

import com.micxzie.dormhub.model.Lease;
import com.micxzie.dormhub.service.LeaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/leases")
public class LeaseController {
    
    private final LeaseService leaseService;

    public LeaseController(LeaseService leaseService){
        this.leaseService = leaseService;
    }

    @GetMapping
    public List<Lease> getAllLeases(){
        return leaseService.getAllLeases();
    }

    @GetMapping("/{id}")
    public Lease getLeaseById(@PathVariable Long id){
        return leaseService.getLeaseById(id);
    }

    @PostMapping
    public Lease createLease(@RequestBody Lease lease){
        return leaseService.createLease(lease);
    }

    @DeleteMapping("/{id}")
    public void deleteLease(@PathVariable Long id){
        leaseService.deleteLease(id);
    }
}
