import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SellerService } from '../seller.service';
import { Component, OnInit, ViewChild, ElementRef, ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-manage-products',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-products.html',
  styleUrl: './manage-products.css'
})
export class ManageProducts implements OnInit {

  products: any[] = [];
  categories: any[] = [];

  @ViewChild('productFormSection') productFormSection!: ElementRef;

  editMode = false;
  selectedProductId: number | null = null;

  selectedImage!: File;
  imagePreview: string | ArrayBuffer | null = null;

  
  productForm = {
    productName: '',
    description: '',
    price: null as number | null,
    mrp: null as number | null,
    discount: null as number | null,
    quantity: null as number | null,
    lowStockThreshold: null as number | null,
    isActive: true,
    categoryId: null as number | null
  };

  constructor(
    private sellerService: SellerService,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadCategories();
  }

  loadProducts() {
    this.sellerService.getAllProducts().subscribe({
      next: (data) => {
        this.products = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Load products failed', err)
    });
  }

  loadCategories() {
    this.sellerService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Load categories failed', err)
    });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (!file) return;

    this.selectedImage = file;

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result;
      this.cdr.detectChanges();
    };
    reader.readAsDataURL(file);
  }

  saveProduct() {
    if (!this.productForm.productName.trim() || !this.productForm.categoryId) {
      alert("Product name & category required");
      return;
    }

    
    const payload = {
      sellerId: 3, 
      productName: this.productForm.productName,
      description: this.productForm.description,
      price: this.productForm.price ?? 0,
      mrp: this.productForm.mrp ?? 0,
      discount: this.productForm.discount ?? 0,
      quantity: this.productForm.quantity ?? 0,
      lowStockThreshold: this.productForm.lowStockThreshold ?? 0,
      isActive: this.productForm.isActive,
      category: {
        categoryId: this.productForm.categoryId
      }
    };

    if (this.editMode && this.selectedProductId) {
      this.sellerService.updateProduct(this.selectedProductId, payload)
        .subscribe({
          next: () => {
            this.loadProducts();
            this.resetForm();
            this.cdr.detectChanges(); 
          },
          error: (err) => console.error('Update failed', err)
        });
    } else {
      if (!this.selectedImage) {
        alert("Please select an image");
        return;
      }

      this.sellerService
        .addProductWithImage(payload, this.selectedImage)
        .subscribe({
          next: () => {
            this.loadProducts();
            this.resetForm();
            this.cdr.detectChanges(); 
          },
          error: (err) => console.error('Add failed', err)
        });
    }
  }

  editProduct(product: any) {
    this.editMode = true;
    this.selectedProductId = product.productId;

    this.productForm = {
      productName: product.productName,
      description: product.description,
      price: product.price,
      mrp: product.mrp,
      discount: product.discount,
      quantity: product.quantity,
      lowStockThreshold: product.lowStockThreshold,
      isActive: product.isActive,
      categoryId: product.category?.categoryId
    };

    this.cdr.detectChanges();

    setTimeout(() => {
      this.productFormSection.nativeElement.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      });
    }, 100);
  }

  deleteProduct(id: number) {
    if (!confirm("Delete this product?")) return;
    this.sellerService.deleteProduct(id).subscribe({
      next: () => {
        this.products = this.products.filter(p => p.productId !== id);
        alert('Product deleted successfully');
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error('Delete failed', err);
        alert('Failed to delete product: ' + (err.error?.error || err.message || 'Unknown error'));
      }
    });
  }

  resetForm() {
    this.editMode = false;
    this.selectedProductId = null;
    this.selectedImage = undefined as any;
    this.imagePreview = null;

    this.productForm = {
      productName: '',
      description: '',
      price: null,
      mrp: null,
      discount: null,
      quantity: null,
      lowStockThreshold: null,
      isActive: true,
      categoryId: null
    };
    this.cdr.detectChanges(); 
  }
}