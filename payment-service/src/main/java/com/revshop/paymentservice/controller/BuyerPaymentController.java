package com.revshop.paymentservice.controller;

import com.revshop.paymentservice.dto.PaymentRequestDTO;
import com.revshop.paymentservice.model.Payment;
import com.revshop.paymentservice.model.PaymentStatus;
import com.revshop.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer/payment")
public class BuyerPaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping("/pay")
    public ResponseEntity<String> payForOrder(
            @Valid @RequestBody PaymentRequestDTO request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        // Validate card details for card payments
        if (request.getType() == com.revshop.paymentservice.model.PaymentType.CREDIT_CARD || 
            request.getType() == com.revshop.paymentservice.model.PaymentType.DEBIT_CARD) {
            if (request.getCardNumber() == null || request.getCardNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Card number is required for card payments");
            }
            if (request.getCardHolderName() == null || request.getCardHolderName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Card holder name is required");
            }
            if (request.getCardExpiry() == null || request.getCardExpiry().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Card expiry is required");
            }
            
            // Basic card number validation (16 digits)
            String cardNum = request.getCardNumber().replaceAll("\\s+", "");
            if (!cardNum.matches("\\d{16}")) {
                return ResponseEntity.badRequest().body("Invalid card number. Must be 16 digits");
            }
            
            // Basic expiry validation (MM/YY format)
            if (!request.getCardExpiry().matches("(0[1-9]|1[0-2])/\\d{2}")) {
                return ResponseEntity.badRequest().body("Invalid expiry format. Use MM/YY");
            }
        }
        
        // Validate UPI ID for UPI payments
        if (request.getType() == com.revshop.paymentservice.model.PaymentType.UPI) {
            if (request.getUpiId() == null || request.getUpiId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("UPI ID is required for UPI payments");
            }
            if (!request.getUpiId().matches(".+@.+")) {
                return ResponseEntity.badRequest().body("Invalid UPI ID format. Must contain @");
            }
        }
        
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setType(request.getType());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setUserId(userId);
        
        Payment processedPayment = paymentService.processPayment(payment);
        
        if (processedPayment.getStatus() == PaymentStatus.SUCCESS) {
            return ResponseEntity.ok("Payment successful! Transaction ID: " + processedPayment.getTransactionId());
        } else {
            return ResponseEntity.status(400).body("Payment failed: " + processedPayment.getGatewayResponse());
        }
    }
}
