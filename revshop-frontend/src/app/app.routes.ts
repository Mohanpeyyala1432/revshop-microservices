import { Routes } from '@angular/router';

import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { ForgotPassword } from './features/auth/forgot-password/forgot-password';
import { VerifyOtp } from './features/auth/verify-otp/verify-otp';
import { ResetPassword } from './features/auth/reset-password/reset-password';

import { ProductList } from './features/buyer/product-list/product-list';
import { Dashboard } from './features/seller/dashboard/dashboard';

export const routes: Routes = [

  
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'forgot-password', component: ForgotPassword },
  { path: 'verify-otp', component: VerifyOtp },
  { path: 'reset-password', component: ResetPassword },

  { path: 'buyer', component: ProductList },

 {
  path: 'seller',
  loadChildren: () =>
    import('./features/seller/seller.routes')
      .then(m => m.SELLER_ROUTES)},

      {
  path: 'buyer',
  loadChildren: () =>
    import('./features/buyer/buyer.routes')
      .then(m => m.BUYER_ROUTES)
},

 
  { path: '**', redirectTo: 'login' }
];