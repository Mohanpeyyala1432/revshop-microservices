import { Routes } from '@angular/router';
import { ProductList } from './product-list/product-list';
import { ProductDetail } from './product-detail/product-detail';
import { Cart } from './cart/cart';
import { Checkout } from './checkout/checkout';
import { OrderHistory } from './Order-history/order-history';

import { Wishlist } from './wishlist/wishlist';
export const BUYER_ROUTES: Routes = [
  { path: '', component: ProductList },                 
  { path: 'product/:name', component: ProductDetail },  
   { path: 'cart', component: Cart },
   { path: 'checkout', component: Checkout },
   {path:'orders',component:OrderHistory},
   { path: 'wishlist', component: Wishlist }
];