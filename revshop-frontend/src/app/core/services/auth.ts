import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = '/api/auth';

  constructor(private http: HttpClient) {}

  register(user: any) {
    return this.http.post(
      `${this.baseUrl}/register`,
      user,
      { responseType: 'text' }
    );
  }

  login(credentials: any) {
    return this.http.post(`${this.baseUrl}/login`, credentials)
      .pipe(
        tap((response: any) => {
          localStorage.setItem('token', response.token);
          localStorage.setItem('role', response.role);
        })
      );
  }

  logout() {
    localStorage.clear();
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  forgotPassword(email: string) {
  return this.http.post(
    `${this.baseUrl}/forgot-password`,
    { email: email },
    {responseType:'text'}
  );
}

verifyOtp(email: string, otp: string) {
  return this.http.post(
    `${this.baseUrl}/verify-otp`,
    { email: email, otp: otp },
    {responseType:'text'}
  );
}

resetPassword(email: string, newPassword: string) {
  return this.http.post(
    `${this.baseUrl}/reset-password`,
    { email: email, newPassword: newPassword },
     {responseType:'text'}
  );
}
}