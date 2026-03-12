import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Product {
  productId: number;
  productName: string;
  description: string;
  price: number;
  mrp: number;
  discount: number;
  imageName: string;
  quantity:number;
  category: {
    categoryName: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class BuyerService {

  private baseUrl = '/api/buyer';

  constructor(private http: HttpClient) {}

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.baseUrl}/products`);
  }

  searchProducts(keyword: string): Observable<Product[]> {
    return this.http.get<Product[]>(
      `${this.baseUrl}/products/search?keyword=${keyword}`
    );
  }

  getProductsByCategory(category: string): Observable<Product[]> {
    return this.http.get<Product[]>(
      `${this.baseUrl}/products/category/${category}`
    );
  }

  getProductByName(productName: string): Observable<Product> {
    return this.http.get<Product>(
      `${this.baseUrl}/products/name/${productName}`
    );
  }

  getAllCategories() {
    return this.http.get<any[]>(
      `${this.baseUrl}/categories`
    );
  }

  addToCart(productId: number, quantity: number) {
    return this.http.post(
      `${this.baseUrl}/cart/add`,
      {
        productId,
        quantity
      }
    );
  }

  getCart() {
    return this.http.get<any>(
      `${this.baseUrl}/cart/view`
    );
  }

  updateCart(cartItemId: number, quantity: number) {
    return this.http.put(
      `${this.baseUrl}/cart/update`,
      {
        cartItemId,
        quantity
      }
    );
  }

  removeCartItem(cartItemId: number) {
    return this.http.delete(
      `${this.baseUrl}/cart/item/${cartItemId}`
    );
  }

  buyNow(productId: number, quantity: number, shipping: string, billing: string) {
    return this.http.post<any>(
      `${this.baseUrl}/order/buy-now`,
      {
        productId,
        quantity,
        shippingAddress: shipping,
        billingAddress: billing
      }
    );
  }

  checkoutCart(shipping: string, billing: string) {
    return this.http.post<any>(
      `${this.baseUrl}/order/checkout`,
      {
        shippingAddress: shipping,
        billingAddress: billing
      }
    );
  }

  payCOD(orderId: number, amount: number) {
    return this.http.post(
      `${this.baseUrl}/payment/pay`,
      {
        orderId,
        amount,
        type: 'COD'
      },
      { responseType: 'text' }
    );
  }

  payCard(orderId: number, amount: number, cardDetails: any) {
    return this.http.post(
      `${this.baseUrl}/payment/pay`,
      {
        orderId,
        amount,
        ...cardDetails
      },
      { responseType: 'text' }
    );
  }

  getOrderHistory() {
    return this.http.get<any[]>(
      `${this.baseUrl}/order/history`
    );
  }

  addToWishlist(productId: number) {
    return this.http.post(
      `${this.baseUrl}/wishlist/${productId}`,
      {},
      { responseType: 'text' }
    );
  }

  getWishlist() {
    return this.http.get<any[]>(
      `${this.baseUrl}/wishlist`
    );
  }

  removeFromWishlist(productId: number) {
    return this.http.delete(
      `${this.baseUrl}/wishlist/${productId}`,
      { responseType: 'text' }
    );
  }

  addReview(productId: number, rating: number, comment: string) {
    return this.http.post(
      `${this.baseUrl}/reviews/${productId}`,
      {
        rating,
        comment
      }
    );
  }

  getReviewsByProduct(productId: number) {
    return this.http.get<any[]>(
      `${this.baseUrl}/reviews/${productId}`
    );
  }

  getNotifications() {
    return this.http.get<any[]>(
      `${this.baseUrl}/notifications`
    );
  }

  markNotificationAsRead(notificationId: number) {
    return this.http.put(
      `${this.baseUrl}/notifications/${notificationId}/read`,
      {},
      { responseType: 'text' }
    );
  }

  cancelOrder(orderId: number) {
    return this.http.put(
      `${this.baseUrl}/order/cancel/${orderId}`,
      {},
      { responseType: 'text' }
    );
  }

}