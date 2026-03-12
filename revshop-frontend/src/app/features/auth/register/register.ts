import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrl:'./register.css'
})
export class Register {

  user = {
    name: '',
    email: '',
    password: '',
    phone: '',
    role: '',
    businessName: '',
    street: '',
    city: '',
    state: '',
    pincode: ''
  };
  hidePassword=true;

  constructor(private auth: AuthService,
              private router: Router) {}

  register() {
    this.auth.register(this.user).subscribe({
      next: () => {
        alert('Registration Successful');
        this.router.navigate(['/login']);
      },
      error: () => {
        alert('Registration Failed');
      }
    });
  }
}