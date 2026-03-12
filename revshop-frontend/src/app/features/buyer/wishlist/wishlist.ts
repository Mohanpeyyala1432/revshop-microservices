import { Component, OnInit, ChangeDetectorRef } from '@angular/core'; 
import { CommonModule } from '@angular/common';
import { BuyerService } from '../buyer.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-wishlist',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './wishlist.html',
  styleUrls: ['./wishlist.css']
})
export class Wishlist implements OnInit {

  wishlist: any[] = [];

  constructor(
    private buyerService: BuyerService,
    private router: Router,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit(): void {
    this.loadWishlist();
  }

  loadWishlist() {
    this.buyerService.getWishlist().subscribe({
      next: (res) => {
        this.wishlist = res;
        this.cdr.detectChanges(); 
      },
      error: (err) => console.error('Failed to load wishlist', err)
    });
  }

  remove(productId: number) {
  this.buyerService.removeFromWishlist(productId)
    .subscribe(() => this.loadWishlist());
}

  viewProduct(productName: string) {
    const encoded = encodeURIComponent(productName);
    this.router.navigate(['/buyer/product', encoded]);
  }
}