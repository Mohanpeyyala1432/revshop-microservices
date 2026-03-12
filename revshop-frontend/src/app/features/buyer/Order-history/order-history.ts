import { Component, OnInit, ChangeDetectorRef } from '@angular/core'; 
import { CommonModule } from '@angular/common';
import { BuyerService } from '../buyer.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './order-history.html',
  styleUrls: ['./order-history.css']
})
export class OrderHistory implements OnInit {

  orders: any[] = [];
  selectedProductId: number | null = null;
rating: number = 5;
comment: string = '';
reviewSuccess: boolean = false;
reviewedProducts: { [key: number]: boolean } = {};

  constructor(
    private buyerService: BuyerService,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders() {
  this.buyerService.getOrderHistory().subscribe({
    next: (res: any[]) => {
     
      this.orders = res;
      
      this.cdr.detectChanges();

      this.orders.forEach(order => {
        if (order.items) {
          order.items.forEach((item: any) => {
            this.buyerService
              .getProductByName(item.productName)
              .subscribe({
                next: (product) => {
                  item.imageName = product.imageName;
                  this.cdr.detectChanges(); 
                },
                error: (err) => console.error(`Image fetch failed for ${item.productName}:`, err)
              });
              
              // Only check if not already marked as reviewed
              if (!this.reviewedProducts[item.productId]) {
                this.checkIfReviewed(item.productId);
              }
          });
        }
      });
    },
    error: (err) => console.error("Order history error:", err)
  });
}

openReview(productId: number) {
  this.selectedProductId = productId;
  this.reviewSuccess = false;
}

submitReview(productId: number) {

  if (!this.comment.trim()) {
    alert("Please enter a comment");
    return;
  }

  this.buyerService
    .addReview(productId, this.rating, this.comment)
    .subscribe({
      next: () => {

        this.reviewedProducts[productId] = true;

        this.reviewSuccess = true;
        this.comment = '';
        this.rating = 5;
        this.selectedProductId = null;

        this.cdr.detectChanges();

        setTimeout(() => {
          this.reviewSuccess = false;
        }, 2000);
      },

      error: (err) => {

        console.error("Review error:", err);

        let errorMsg = '';
        if (typeof err.error === 'string') {
          errorMsg = err.error;
        } else if (err.error?.message) {
          errorMsg = err.error.message;
        } else if (err.message) {
          errorMsg = err.message;
        }

        if (errorMsg.includes("already reviewed")) {
          this.reviewedProducts[productId] = true;
          this.selectedProductId = null;
          alert("Review already submitted");
        } else if (errorMsg.includes("purchased")) {
          alert("You can only review products you have purchased");
        } else {
          alert("Failed to submit review. Please try again.");
        }

        this.cdr.detectChanges();
      }
    });
}

checkIfReviewed(productId: number) {

  this.buyerService
    .getReviewsByProduct(productId)
    .subscribe({
      next: (data: any) => {

        const currentUser = localStorage.getItem('username');
        const reviews = data.reviews || [];

        const alreadyReviewed = reviews.some(
          (r: any) => r.userName === currentUser
        );

        if (alreadyReviewed) {
          this.reviewedProducts[productId] = true;
          this.cdr.detectChanges();
        }
      },
      error: (err) => console.error("Review check failed:", err)
    });
}

cancelOrder(orderId: number) {
  if (confirm('Are you sure you want to cancel this order?')) {
    this.buyerService.cancelOrder(orderId).subscribe({
      next: (response) => {
        alert(response);
        this.loadOrders();
      },
      error: (err) => {
        console.error('Cancel order error:', err);
        alert('Failed to cancel order');
      }
    });
  }
}
}