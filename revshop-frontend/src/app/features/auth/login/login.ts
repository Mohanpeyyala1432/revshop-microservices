import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrl:'./login.css'
})
export class Login {

  email = '';
  password = '';
  hidePassword=true;

  constructor(private auth: AuthService,
              private router: Router) {}

onLogin() {
  this.auth.login({
    email: this.email,
    password: this.password
  }).subscribe({
    next: (response: any) => {

      localStorage.setItem('token', response.token);
      localStorage.setItem('role', response.role);
     localStorage.setItem('username', response.username || 'Buyer');

      if (response.role === 'BUYER') {
        this.router.navigate(['/buyer'], { replaceUrl: true });
      } 
      else if (response.role === 'SELLER') {
        this.router.navigate(['/seller'], { replaceUrl: true });
      }

    },
    error: (error) => {
      if (error.status === 429) {
        alert('Too many login attempts! Please try again after 30 seconds.');
      } else {
        alert('Invalid Credentials');
      }
    }
  });
}

}
