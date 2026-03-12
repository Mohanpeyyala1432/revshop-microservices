import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SellerService {

  private baseUrl = '/api/seller';

  constructor(private http: HttpClient) {}



  getCategories(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/categories?ts=${new Date().getTime()}`
    );
  }

  addCategory(category: any): Observable<any> {
    return this.http.post<any>(
      `${this.baseUrl}/categories`,
      category
    );
  }

  updateCategory(id: number, category: any): Observable<any> {
    return this.http.put<any>(
      `${this.baseUrl}/categories/update/${id}`,
      category
    );
  }

  deleteCategory(id: number): Observable<any> {
    return this.http.delete<any>(
      `${this.baseUrl}/categories/${id}`
    );
  }



    getAllProducts() {
    return this.http.get<any[]>(`${this.baseUrl}/products/all`);
  }

  addProductWithImage(product: any, imageFile: File) {

    const formData = new FormData();

    formData.append('product', JSON.stringify(product));
    formData.append('image', imageFile);

    return this.http.post<any>(
      `${this.baseUrl}/products/add-with-image`,
      formData
    );
  }

  updateProduct(id: number, product: any) {
    return this.http.put<any>(
      `${this.baseUrl}/products/update/${id}`,
      product
    );
  }

 deleteProduct(id: number) {
  return this.http.delete(
    `${this.baseUrl}/products/delete/${id}`,
    {
      headers: {
        'X-User-Id': '3'   
      }
    }
  );
}

  getLowStockCount(): Observable<number> {
    return this.http.get<number>(
      `${this.baseUrl}/products/low-stock/count`
    );
  }

  getLowStockProducts(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/products/low-stock`
    );
  }

  getInventory(productId: number): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/products/inventory/${productId}`
    );
  }

  

  getSellerOrders(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/orders/all`
    );
  }

  updateOrderStatus(orderId: number, status: string): Observable<any> {
    return this.http.put(
      `${this.baseUrl}/orders/update-status/${orderId}?status=${status}`,
      {},
      { responseType: 'text' }
    );
  }

  getNotifications(): Observable<any[]> {
    return this.http.get<any[]>('/api/notifications/seller');
  }

  markNotificationAsRead(notificationId: number): Observable<any> {
    return this.http.put(
      `/api/notifications/${notificationId}/read`,
      {},
      { responseType: 'text' }
    );
  }

}