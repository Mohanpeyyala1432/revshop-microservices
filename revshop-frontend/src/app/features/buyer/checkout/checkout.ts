import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BuyerService } from '../buyer.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './checkout.html',
  styleUrls: ['./checkout.css']
})
export class Checkout {

  productId!: number;
  quantity!: number;
  isCartCheckout = false;
  orderAmount = 0;

  shippingAddress = '';
  billingAddress = '';

  orderSuccess =false;
  successMessage='';

  type: 'COD' | 'DEBIT_CARD' | 'CREDIT_CARD' | 'UPI' = 'COD';

  cardNumber = '';
  cardHolderName = '';
  cardExpiry = '';
  upiId = '';

  constructor(
    private router: Router,
    private buyerService: BuyerService,
    private cdr: ChangeDetectorRef
  ) {
    const state = history.state;

    if (state && state.productId) {
      this.productId = state.productId;
      this.quantity = state.quantity;
      this.isCartCheckout = false;
    } else if (state && state.fromCart) {
      this.isCartCheckout = true;
    } else {
      this.router.navigate(['/buyer']);
    }
  }

 placeOrder() {
  if (!this.shippingAddress || !this.billingAddress) {
    alert("Please enter shipping & billing address");
    return;
  }

  if (this.type === 'DEBIT_CARD' || this.type === 'CREDIT_CARD') {
    if (!this.cardNumber || !this.cardHolderName || !this.cardExpiry) {
      alert("Please fill card details");
      return;
    }
  }

  if (this.type === 'UPI' && !this.upiId) {
    alert("Please enter UPI ID");
    return;
  }

  if (this.isCartCheckout) {
    this.checkoutFromCart();
  } else {
    this.buyNowCheckout();
  }
}

checkoutFromCart() {
  this.buyerService.checkoutCart(
    this.shippingAddress,
    this.billingAddress
  ).subscribe({
    next: (orderRes: any) => {
      const orderId = orderRes.orderId;
      this.orderAmount = orderRes.totalAmount || 0;
      this.processPayment(orderId);
    },
    error: (err: any) => {
      console.error("Cart Checkout Failed:", err);
      if (err.status === 503) {
        alert(err.error?.error || 'Service temporarily unavailable. Please try again later.');
      } else {
        alert('Checkout failed. Please try again.');
      }
    }
  });
}

buyNowCheckout() {
  this.buyerService.buyNow(
    this.productId,
    this.quantity,
    this.shippingAddress,
    this.billingAddress
  ).subscribe({
    next: (orderRes: any) => {
      const orderId = orderRes.orderId;
      this.orderAmount = orderRes.totalAmount || 0;
      this.processPayment(orderId);
    },
    error: (err) => {
      console.error("Order Creation Failed:", err);
      if (err.status === 503) {
        alert(err.error?.error || 'Service temporarily unavailable. Please try again later.');
      } else {
        alert('Order creation failed. Please try again.');
      }
    }
  });
}

processPayment(orderId: number) {
  const handleSuccess = () => {
    this.orderSuccess = true;
    this.successMessage = "🎉 Order placed successfully!";
    this.resetForm();
    this.cdr.detectChanges();
    setTimeout(() => {
      this.router.navigate(['/buyer']);
    }, 2000);
  };

  if (this.type === 'COD') {
    this.buyerService.payCOD(orderId, this.orderAmount).subscribe({
      next: handleSuccess,
      error: (err) => console.error("COD Payment Error:", err)
    });
  } else if (this.type === 'UPI') {
    this.buyerService.payCard(orderId, this.orderAmount, {
      type: this.type,
      upiId: this.upiId
    }).subscribe({
      next: handleSuccess,
      error: (err) => {
        console.error("UPI Payment Error:", err);
        alert("Payment failed. Please try again.");
      }
    });
  } else {
    this.buyerService.payCard(orderId, this.orderAmount, {
      type: this.type,
      cardNumber: this.cardNumber,
      cardHolderName: this.cardHolderName,
      cardExpiry: this.cardExpiry
    }).subscribe({
      next: handleSuccess,
      error: (err) => {
        console.error("Card Payment Error:", err);
        alert("Payment failed. Please try again.");
      }
    });
  }
}

resetForm() {
  this.shippingAddress = '';
  this.billingAddress = '';
  this.cardNumber = '';
  this.cardHolderName = '';
  this.cardExpiry = '';
  this.upiId = '';
  this.type = 'COD';
}
}