package com.revshop.paymentservice.service;

import com.revshop.paymentservice.model.Payment;
import java.util.Optional;

public interface PaymentService {
    Payment processPayment(Payment payment);
    Optional<Payment> getPaymentByOrderId(Long orderId);
}
