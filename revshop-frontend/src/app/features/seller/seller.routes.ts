import { Routes } from '@angular/router';
import { SellerLayout } from './layout/seller-layout';
import { Dashboard } from './dashboard/dashboard';
import { AddProduct } from './add-product/add-product';
import { ManageProducts } from './manage-products/manage-products';
import { SellerOrders } from './seller-orders/seller-orders';
import { Category } from './categories/category';

export const SELLER_ROUTES: Routes = [
  {
    path: '',
    component: SellerLayout,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: Dashboard },
      { path: 'add-product', component: AddProduct },
      { path: 'manage-products', component: ManageProducts },
      { path: 'orders', component: SellerOrders },
      { path: 'categories', component: Category }
    ]
  }
];