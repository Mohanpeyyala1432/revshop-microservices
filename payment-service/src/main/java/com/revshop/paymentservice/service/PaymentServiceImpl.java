package com.revshop.paymentservice.service;

import com.revshop.paymentservice.model.Payment;
import com.revshop.paymentservice.model.PaymentStatus;
import com.revshop.paymentservice.model.PaymentType;
import com.revshop.paymentservice.repository.PaymentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    
    private static final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    private final Random random = new Random();
    
    public Payment processPayment(Payment payment) {
        logger.info("Processing payment for Order ID: {}, Amount: {}, Type: {}", 
                payment.getOrderId(), payment.getAmount(), payment.getType());
        
        // Generate unique transaction ID
        String transactionId = generateTransactionId(payment.getType());
        payment.setTransactionId(transactionId);
        payment.setPaymentDate(LocalDateTime.now());
        
        // Simulate payment gateway processing
        simulatePaymentGateway(payment);
        
        // Save payment record
        Payment savedPayment = paymentRepository.save(payment);
        
        logger.info("Payment processed - Transaction ID: {}, Status: {}", 
                transactionId, savedPayment.getStatus());
        
        return savedPayment;
    }
    
    public Optional<Payment> getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
    
    private void simulatePaymentGateway(Payment payment) {
        try {
            // Simulate API call delay (100-500ms)
            Thread.sleep(100 + random.nextInt(400));
            
            if (payment.getType() == PaymentType.COD) {
                // COD always succeeds
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setGatewayResponse("COD order placed successfully");
            } else {
                // Online payments: 95% success rate
                boolean isSuccess = random.nextInt(100) < 95;
                
                if (isSuccess) {
                    payment.setStatus(PaymentStatus.SUCCESS);
                    payment.setGatewayResponse("Payment successful - Gateway approved");
                } else {
                    payment.setStatus(PaymentStatus.FAILED);
                    payment.setGatewayResponse("Payment declined - Insufficient funds or gateway error");
                }
            }
        } catch (InterruptedException e) {
            logger.error("Payment processing interrupted", e);
            payment.setStatus(PaymentStatus.FAILED);
            payment.setGatewayResponse("Payment processing error");
            Thread.currentThread().interrupt();
        }
    }
    
    private String generateTransactionId(PaymentType type) {
        String prefix = switch (type) {
            case COD -> "COD";
            case CREDIT_CARD -> "CC";
            case DEBIT_CARD -> "DC";
            case UPI -> "UPI";
        };
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
