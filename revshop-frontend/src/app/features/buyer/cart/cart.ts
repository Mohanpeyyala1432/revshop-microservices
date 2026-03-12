import { Component, OnInit,ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BuyerService } from '../buyer.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cart.html',
  styleUrls: ['./cart.css']
})
export class Cart implements OnInit {

  cartItems: any[] = [];
  totalAmount: number = 0;

  constructor(private buyerService: BuyerService,
    private cdr:ChangeDetectorRef,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }


  loadCart() {
  this.buyerService.getCart().subscribe({
    next: (res) => {

      this.totalAmount = res.totalAmount;

      const items = res.items;

      // For each cart item, fetch product image
      items.forEach((item: any) => {

        this.buyerService
          .getProductByName(item.productName)
          .subscribe(product => {

            item.imageName = product.imageName;
            this.cdr.detectChanges();
          });

      });

      this.cartItems = items;
      this.cdr.detectChanges();

    },
    error: (err) => console.error(err)
  });
}


increase(item: any) {
  const newQty = item.quantity + 1;

  this.buyerService.updateCart(item.cartItemId, newQty)
    .subscribe({
      next: () => {
        item.quantity = newQty;   
        this.loadCart();          
      },
      error: (err) => {
        console.error("Update failed", err);
      }
    });
}

decrease(item: any) {
  if (item.quantity > 1) {
    const newQty = item.quantity - 1;

    this.buyerService.updateCart(item.cartItemId, newQty)
      .subscribe({
        next: () => {
          item.quantity = newQty;
          this.loadCart();
        },
        error: (err) => {
          console.error("Update failed", err);
        }
      });
  }
}

update(item: any) {
  this.buyerService
    .updateCart(item.cartItemId, item.quantity)
    .subscribe(() => this.loadCart());
}

remove(cartItemId: number) {
  this.buyerService
    .removeCartItem(cartItemId)
    .subscribe(() => this.loadCart());
}

buyNow(item: any) {
  this.router.navigate(['/buyer/checkout'], {
    state: {
      productId: item.productId,
      quantity: item.quantity,
      fromCart:true
    }
  });
}

proceedToCheckout() {
  this.router.navigate(['/buyer/checkout'], {
    state: { fromCart: true }
  });
}
}