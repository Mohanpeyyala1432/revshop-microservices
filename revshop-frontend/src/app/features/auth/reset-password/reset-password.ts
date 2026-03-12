import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css'
})
export class ResetPassword {

  email = '';
  newPassword = '';
  confirmPassword = '';
  isLoading = false;
  hideNewPassword = true;
  hideConfirmPassword = true;

  constructor(
    private auth: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.route.queryParams.subscribe(params => {
      this.email = params['email'];
    });
  }

  resetPassword() {

    if (!this.newPassword || !this.confirmPassword) return;

    if (this.newPassword !== this.confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    this.isLoading = true;

    this.auth.resetPassword(this.email, this.newPassword).subscribe({
      next: () => {
        this.isLoading = false;
        alert('Password reset successful');

        
        this.router.navigate(['/login']);
      },
      error: () => {
        this.isLoading = false;
        alert('Failed to reset password');
      }
    });
  }
}