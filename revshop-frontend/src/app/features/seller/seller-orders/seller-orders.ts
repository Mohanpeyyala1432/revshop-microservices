import { Component, OnInit, ChangeDetectorRef } from '@angular/core'; 
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SellerService } from '../seller.service';

@Component({
  selector: 'app-seller-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './seller-orders.html',
  styleUrl: './seller-orders.css'
})
export class SellerOrders implements OnInit {

  orders: any[] = [];
  totalRevenue: number = 0;

  constructor(
    private sellerService: SellerService,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders() {
    this.sellerService.getSellerOrders().subscribe({
      next: (data) => {
        this.orders = data;

        this.totalRevenue = this.orders
          .reduce((sum, order) => sum + order.totalAmount, 0);

        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error('Failed to load seller orders', err);
      }
    });
  }

  updateStatus(orderId: number, status: string) {
    this.sellerService.updateOrderStatus(orderId, status).subscribe({
      next: () => {
        alert('Order status updated successfully!');
        this.loadOrders();
      },
      error: (err) => {
        console.error('Failed to update order status', err);
        alert('Failed to update order status');
      }
    });
  }
}