import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService } from '../../../core/services/auth';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-verify-otp',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './verify-otp.html',
  styleUrl: './verify-otp.css'
})
export class VerifyOtp implements OnInit, OnDestroy {

  email = '';
  otp = '';
  isLoading = false;

  countdown = 60;
  timer: any;

  constructor(
    private auth: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.email = params['email'];
    });

    this.startTimer();
  }

  startTimer() {
    this.countdown = 30;

    this.timer = setInterval(() => {
      if (this.countdown > 0) {
        this.countdown--;
      } else {
        clearInterval(this.timer);
      }
    }, 1000);
  }

  verifyOtp() {
    if (!this.otp) return;

    this.isLoading = true;

    this.auth.verifyOtp(this.email, this.otp).subscribe({
      next: () => {
        this.isLoading = false;

        this.router.navigate(['/reset-password'], {
          queryParams: { email: this.email }
        });
      },
      error: () => {
        this.isLoading = false;
        alert('Invalid or expired OTP');
      }
    });
  }

  resendOtp() {
    if (this.countdown > 0) return;

    this.auth.forgotPassword(this.email).subscribe({
      next: () => {
        alert('OTP resent successfully');
        this.startTimer();
      },
      error: () => {
        alert('Failed to resend OTP');
      }
    });
  }

  ngOnDestroy() {
    clearInterval(this.timer);
  }
}