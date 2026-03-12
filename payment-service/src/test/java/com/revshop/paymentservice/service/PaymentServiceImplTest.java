package com.revshop.paymentservice.service;

import com.revshop.paymentservice.model.Payment;
import com.revshop.paymentservice.model.PaymentStatus;
import com.revshop.paymentservice.model.PaymentType;
import com.revshop.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setPaymentId(1L);
        payment.setOrderId(1L);
        payment.setAmount(100.0);
        payment.setType(PaymentType.CREDIT_CARD);
        payment.setStatus(PaymentStatus.PENDING);
    }

    @Test
    void processPayment_COD_Success() {
        payment.setType(PaymentType.COD);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);

        Payment result = paymentService.processPayment(payment);

        assertNotNull(result);
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertTrue(result.getTransactionId().startsWith("COD-"));
        assertEquals("COD order placed successfully", result.getGatewayResponse());
        verify(paymentRepository).save(payment);
    }

    @Test
    void processPayment_OnlinePayment_Processed() {
        // Since random behavior is hard to strictly assert without mocking the Random class itself,
        // we'll verify it returns a saved payment with a transaction ID, and either success or failure.
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);

        Payment result = paymentService.processPayment(payment);

        assertNotNull(result);
        assertTrue(result.getStatus() == PaymentStatus.SUCCESS || result.getStatus() == PaymentStatus.FAILED);
        assertNotNull(result.getTransactionId());
        assertTrue(result.getTransactionId().startsWith("CC-"));
        verify(paymentRepository).save(payment);
    }

    @Test
    void getPaymentByOrderId_Found() {
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));

        Optional<Payment> result = paymentService.getPaymentByOrderId(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getOrderId());
    }

    @Test
    void getPaymentByOrderId_NotFound() {
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());

        Optional<Payment> result = paymentService.getPaymentByOrderId(1L);

        assertFalse(result.isPresent());
    }
}
