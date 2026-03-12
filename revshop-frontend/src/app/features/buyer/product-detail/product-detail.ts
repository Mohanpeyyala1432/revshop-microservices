import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BuyerService, Product } from '../buyer.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-detail.html',
  styleUrls: ['./product-detail.css']
})
export class ProductDetail implements OnInit {

  product!: Product;

  selectedQty: number = 1;

  reviews: any[] = [];
  averageRating: number = 0;
  totalReviews: number = 0;

  constructor(
    private route: ActivatedRoute,
    private buyerService: BuyerService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) {}

  ngOnInit(): void {

    this.route.paramMap.subscribe(params => {
      const name = params.get('name');

      if (name) {
        const decodedName = decodeURIComponent(name);

        this.buyerService.getProductByName(decodedName)
          .subscribe({
            next: (res) => {

              this.product = res;
              this.selectedQty = 1;

              
              this.loadReviews();

              this.cdr.detectChanges();
            },
            error: (err) => {
              console.error("Error loading product:", err);
            }
          });
      }
    });

  }

  
  loadReviews() {

    if (!this.product?.productId) return;

    this.buyerService
      .getReviewsByProduct(this.product.productId)
      .subscribe({
        next: (data: any) => {

          this.averageRating = data.averageRating || 0;
          this.totalReviews = data.totalReviews || 0;
          this.reviews = data.reviews || [];

          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error("Review load error:", err);
        }
      });
  }

  increaseQty() {
    if (this.selectedQty < this.product.quantity) {
      this.selectedQty++;
    }
  }

  decreaseQty() {
    if (this.selectedQty > 1) {
      this.selectedQty--;
    }
  }

  addToCart() {

    if (!this.product?.productId) {
      console.error("Product ID missing");
      return;
    }

    this.buyerService
      .addToCart(this.product.productId, this.selectedQty)
      .subscribe({
        next: () => {
          alert("Added to cart");
        },
        error: () => {
          alert("Failed to add");
        }
      });
  }

  buyNow() {
    this.router.navigate(['/buyer/checkout'], {
      state: {
        productId: this.product.productId,
        quantity: this.selectedQty,
        fromCart: false
      }
    });
  }

}