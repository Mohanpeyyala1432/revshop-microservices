import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './forgot-password.html',
  styleUrl:'./forgot-password.css'
})
export class ForgotPassword {

  email = '';
  isLoading = false;

  constructor(private auth: AuthService,
              private router: Router) {}

  sendOtp() {

    this.isLoading = true;

    this.auth.forgotPassword(this.email).subscribe({
      next: (response:string) => {
        this.isLoading = false;
        alert("your otp is:"+response);
        this.router.navigate(['/verify-otp'], {
          queryParams: { email: this.email }
        });
      },
      error: (err) => {
        this.isLoading = false;
        console.error(err);
        alert('Email not found or server error');
      }
    });
  }
}