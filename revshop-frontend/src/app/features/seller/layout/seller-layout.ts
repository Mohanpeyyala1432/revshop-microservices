import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-seller-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './seller-layout.html',
  styleUrl: './seller-layout.css'
})
export class SellerLayout {
  constructor(private router: Router) {}

  logout() {
    localStorage.removeItem('token');   
    localStorage.removeItem('role');    
    this.router.navigate(['/login']);
  }
}