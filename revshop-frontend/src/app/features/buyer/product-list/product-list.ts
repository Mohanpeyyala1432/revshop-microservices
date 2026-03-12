import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { BuyerService } from '../buyer.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-list.html',
  styleUrls: ['./product-list.css']
})
export class ProductList implements OnInit {

  products: any[] = [];
  searchText: string = '';
  userName: string = '';

  selectedCategory: string = 'All';
  categories: any[] = [];

  notifications: any[] = [];
  unreadCount: number = 0;
  showNotifications: boolean = false;

  constructor(
    private buyerService: BuyerService,
    private router: Router,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit(): void {
    this.loadAllProducts();
    this.loadCategories();
    this.loadNotifications();

   
    this.userName = localStorage.getItem('username') || 'Buyer';
  }

 loadAllProducts() {
  this.buyerService.getAllProducts().subscribe({
    next: (res) => {

      this.products = res;

      this.products.forEach(product => {

        this.buyerService
          .getReviewsByProduct(product.productId)
          .subscribe({
            next: (reviewData: any) => {

              product.averageRating = reviewData.averageRating || 0;
              product.reviewCount = reviewData.totalReviews || 0;

              this.cdr.detectChanges();
            },
            error: () => {
              product.averageRating = 0;
              product.reviewCount = 0;
            }
          });

      });

    },
    error: (err) => console.error(err)
  });
}

  searchProducts() {

  const keyword = this.searchText.trim().toLowerCase();

  if (!keyword) {
    this.loadAllProducts();
    return;
  }

  this.buyerService.getAllProducts().subscribe({
    next: (res) => {

      this.products = res.filter((product: any) =>

        
        product.productName.toLowerCase().includes(keyword) ||

        product.description.toLowerCase().includes(keyword) ||

        product.category?.categoryName.toLowerCase().includes(keyword)

      ),this.cdr.detectChanges();

    },
    error: (err) => console.error(err)
  });

}

  
  loadCategories() {
    this.buyerService.getAllCategories().subscribe({
      next: (res) => {
        this.categories = res;
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error("Category Load Error:", err);
        
      }
    });
  }

  filterByCategory(categoryName: string) {
    this.buyerService.getProductsByCategory(categoryName).subscribe({
      next: (res) => {
        this.products = res;
        this.cdr.detectChanges(); 
      },
      error: (err) => console.error(err)
    });
  }

  selectCategory(category: string) {
    this.selectedCategory = category;

    if (category === 'All') {
      this.loadAllProducts();
    } else {
      this.filterByCategory(category);
    }
  }

  goToCart() {
    this.router.navigate(['/buyer/cart']);
  }

  goToProfile() {
    this.router.navigate(['/buyer/profile']);
  }

  
  addToCart(product: any) {

  this.buyerService
    .addToCart(product.productId, 1) 
    .subscribe({
      next: (res) => {
        console.log("Cart Response:", res);
        alert(`${product.productName} added to cart`);
      },
      error: (err) => {
        console.error("Cart Error:", err);
        alert("Failed to add to cart");
      }
    });

}


  
  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  viewProduct(productName: string) {
    const encodedName = encodeURIComponent(productName);
    this.router.navigate(['/buyer/product', encodedName]);
  }

  goToOrders() {
  this.router.navigate(['/buyer/orders']);
}

addToWishlist(productId: number) {
  this.buyerService.addToWishlist(productId).subscribe({
    next: () => {
      alert("Added to wishlist");
    },
    error: (err) => {
      console.error("Wishlist error:", err);
      alert("Failed to add to wishlist");
    }
  });
}

goToWishlist() {
  this.router.navigate(['/buyer/wishlist']);
}

loadNotifications() {
  this.buyerService.getNotifications().subscribe({
    next: (res) => {
      this.notifications = res || [];

      this.unreadCount = this.notifications
        .filter(n => !n.readStatus).length;
    },
    error: (err) => console.error("Notification error:", err)
  });
}

toggleNotifications() {
  this.showNotifications = !this.showNotifications;
}

markAsRead(notification: any) {
  if (!notification.readStatus) {
    this.buyerService.markNotificationAsRead(notification.id).subscribe({
      next: () => {
        notification.readStatus = true;
        this.unreadCount = this.notifications.filter(n => !n.readStatus).length;
        this.cdr.detectChanges();
      },
      error: (err) => console.error("Mark as read error:", err)
    });
  }
}

}