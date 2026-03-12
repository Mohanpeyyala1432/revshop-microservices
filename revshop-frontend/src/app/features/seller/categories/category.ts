import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SellerService } from '../seller.service';

@Component({
  selector: 'app-category',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './category.html',
  styleUrl: './category.css'
})
export class Category implements OnInit {

  categories: any[] = [];

  newCategory = {
    categoryName: '',
    description: ''
  };

  editMode = false;
  selectedCategoryId: number | null = null;

  
  constructor(
    private sellerService: SellerService,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories() {
    this.sellerService.getCategories().subscribe({
      next: (data: any) => {
        this.categories = [...data];
        this.cdr.detectChanges(); 
      },
      error: (err) => console.error("Load failed", err)
    });
  }

  saveCategory() {
    if (
      !this.newCategory.categoryName ||
      !this.newCategory.description ||
      this.newCategory.categoryName.trim() === '' ||
      this.newCategory.description.trim() === ''
    ) {
      alert("Category Name and Description are required!");
      return;
    }

    if (this.editMode && this.selectedCategoryId) {
      const updatePayload = {
        name: this.newCategory.categoryName.trim(),
        description: this.newCategory.description.trim()
      };

      this.sellerService.updateCategory(
        this.selectedCategoryId,
        updatePayload
      ).subscribe({
        next: () => {
          this.loadCategories();
          this.resetForm();
          this.cdr.detectChanges(); 
        },
        error: (err) => console.error("Update failed", err)
      });

    } else {
      const addPayload = {
        categoryName: this.newCategory.categoryName.trim(),
        description: this.newCategory.description.trim()
      };

      this.sellerService.addCategory(addPayload)
        .subscribe({
          next: () => {
            this.loadCategories();
            this.resetForm();
            this.cdr.detectChanges(); 
          },
          error: (err) => console.error("Add failed", err)
        });
    }
  }

  editCategory(category: any) {
    this.editMode = true;
    this.selectedCategoryId = category.categoryId;
    this.newCategory.categoryName = category.categoryName;
    this.newCategory.description = category.description;
    this.cdr.detectChanges(); 
  }

  deleteCategory(id: number) {
    if (!confirm("Are you sure you want to delete this category?")) {
      return;
    }

    this.sellerService.deleteCategory(id)
      .subscribe({
        next: () => {
          this.categories = this.categories.filter(
            category => category.categoryId !== id
          );
          this.cdr.detectChanges(); 
        },
        error: (err) => console.error("Delete failed", err)
      });
  }

  resetForm() {
    this.editMode = false;
    this.selectedCategoryId = null;
    this.newCategory = {
      categoryName: '',
      description: ''
    };
    this.cdr.detectChanges(); 
  }
}