package com.micxzie.dormhub.repository;

import com.micxzie.dormhub.model.Payment;
import com.micxzie.dormhub.model.Lease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>{
    Optional<Payment> findByLeaseAndForPeriod(Lease lease, String forPeriod);
}