package com.micxzie.dormhub.service;

import com.micxzie.dormhub.exception.DuplicateResourceException;
import com.micxzie.dormhub.exception.InvalidRequestException;
import com.micxzie.dormhub.exception.ResourceNotFoundException;
import com.micxzie.dormhub.model.Lease;
import com.micxzie.dormhub.model.Payment;
import com.micxzie.dormhub.repository.LeaseRepository;
import com.micxzie.dormhub.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LeaseRepository leaseRepository;

    public PaymentService(PaymentRepository paymentRepository, LeaseRepository leaseRepository) {
        this.paymentRepository = paymentRepository;
        this.leaseRepository = leaseRepository;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    public Payment createPayment(Payment payment) {
        // Step 1: Confirm the lease actually exists
        Lease lease = leaseRepository.findById(payment.getLease().getLeaseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lease not found with id: " + payment.getLease().getLeaseId()));

        // Step 2: Business rule — only ACTIVE leases can receive payments
        if (!lease.getStatus().equals("ACTIVE")) {
            throw new InvalidRequestException("Cannot log a payment against a lease that is not ACTIVE");
        }

        // Step 3: Business rule — one payment per lease per period
        if (paymentRepository.findByLeaseAndForPeriod(lease, payment.getForPeriod()).isPresent()) {
            throw new DuplicateResourceException(
                    "A payment for period " + payment.getForPeriod() + " already exists for this lease");
        }

        // Step 4: Business rule — amount must exactly match the room's monthly rate
        BigDecimal expectedAmount = lease.getRoom().getMonthlyRate();
        if (payment.getAmount() == null || payment.getAmount().compareTo(expectedAmount) != 0) {
            throw new InvalidRequestException(
                    "Payment amount must be exactly " + expectedAmount + " to match the room's monthly rate");
        }

        // Step 5: Attach the real, verified lease before saving
        payment.setLease(lease);

        return paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        Payment existingPayment = getPaymentById(id);
        paymentRepository.delete(existingPayment);
    }
}