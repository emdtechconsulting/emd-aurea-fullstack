import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  computed,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { CategoryResponse } from '../../core/models/category-response';
import { ProductRequest } from '../../core/models/product-request';
import { ProductResponse } from '../../core/models/product-response';
import { CategoryService } from '../../core/services/category.service';
import { ProductService } from '../../core/services/product.service';

@Component({
  selector: 'app-products',
  imports: [ReactiveFormsModule],
  templateUrl: './products.html',
  styleUrl: './products.scss',
})
export class Products implements OnInit {
  private readonly productService = inject(ProductService);
  private readonly categoryService = inject(CategoryService);
  private readonly formBuilder = inject(FormBuilder);

  readonly products = signal<ProductResponse[]>([]);
  readonly categories = signal<CategoryResponse[]>([]);

  readonly loading = signal(true);
  readonly categoriesLoading = signal(true);
  readonly saving = signal(false);
  readonly formVisible = signal(false);

  readonly errorMessage = signal('');
  readonly categoryErrorMessage = signal('');
  readonly saveErrorMessage = signal('');
  readonly successMessage = signal('');

  readonly activeProducts = computed(
    () =>
      this.products().filter(
        (product) => product.active,
      ).length,
  );

  readonly productForm = this.formBuilder.nonNullable.group({
    categoryId: [
      0,
      [
        Validators.required,
        Validators.min(1),
      ],
    ],
    name: [
      '',
      [
        Validators.required,
      ],
    ],
    description: [''],
    active: [true],
    price25: [
      0,
      [
        Validators.required,
        Validators.min(0.01),
      ],
    ],
    price50: [
      0,
      [
        Validators.required,
        Validators.min(0.01),
      ],
    ],
    price100: [
      0,
      [
        Validators.required,
        Validators.min(0.01),
      ],
    ],
  });

  ngOnInit(): void {
    this.loadProducts();
    this.loadCategories();
  }

  loadProducts(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.productService.findAll().subscribe({
      next: (products) => {
        this.products.set(products);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage.set(
          this.resolveLoadErrorMessage(error),
        );
        this.loading.set(false);
      },
    });
  }

  loadCategories(): void {
    this.categoriesLoading.set(true);
    this.categoryErrorMessage.set('');

    this.categoryService.findAll().subscribe({
      next: (categories) => {
        this.categories.set(categories);
        this.categoriesLoading.set(false);

        if (
          categories.length > 0 &&
          this.productForm.controls.categoryId.value === 0
        ) {
          this.productForm.controls.categoryId.setValue(
            categories[0].id,
          );
        }
      },
      error: (error: HttpErrorResponse) => {
        this.categoryErrorMessage.set(
          this.resolveCategoryErrorMessage(error),
        );
        this.categoriesLoading.set(false);
      },
    });
  }

  toggleProductForm(): void {
    const willOpen = !this.formVisible();

    this.formVisible.set(willOpen);
    this.saveErrorMessage.set('');
    this.successMessage.set('');

    if (willOpen) {
      this.resetProductForm();
    }
  }

  saveProduct(): void {
    this.saveErrorMessage.set('');
    this.successMessage.set('');

    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    const formValue = this.productForm.getRawValue();

    const request: ProductRequest = {
      categoryId: formValue.categoryId,
      name: formValue.name.trim(),
      description:
        formValue.description.trim() || null,
      active: formValue.active,
      prices: [
        {
          quantity: 25,
          price: formValue.price25,
        },
        {
          quantity: 50,
          price: formValue.price50,
        },
        {
          quantity: 100,
          price: formValue.price100,
        },
      ],
    };

    this.saving.set(true);

    this.productService.create(request).subscribe({
      next: (createdProduct) => {
        this.products.update(
          (products) => [
            ...products,
            createdProduct,
          ],
        );

        this.successMessage.set(
          `Producto "${createdProduct.name}" registrado correctamente.`,
        );

        this.saving.set(false);
        this.formVisible.set(false);
        this.resetProductForm();
      },
      error: (error: HttpErrorResponse) => {
        this.saveErrorMessage.set(
          this.resolveSaveErrorMessage(error),
        );
        this.saving.set(false);
      },
    });
  }

  private resetProductForm(): void {
    const categoryId =
      this.categories()[0]?.id ?? 0;

    this.productForm.reset({
      categoryId,
      name: '',
      description: '',
      active: true,
      price25: 0,
      price50: 0,
      price100: 0,
    });
  }

  private resolveLoadErrorMessage(
    error: HttpErrorResponse,
  ): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor de AUREA.';
    }

    if (error.status === 401 || error.status === 403) {
      return 'Tu sesión no permite consultar los productos.';
    }

    return 'No fue posible cargar los productos.';
  }

  private resolveCategoryErrorMessage(
    error: HttpErrorResponse,
  ): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor para cargar las categorías.';
    }

    if (error.status === 401 || error.status === 403) {
      return 'Tu sesión no permite consultar las categorías.';
    }

    return 'No fue posible cargar las categorías.';
  }

  private resolveSaveErrorMessage(
    error: HttpErrorResponse,
  ): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor de AUREA.';
    }

    if (error.status === 400) {
      return 'Revisa los datos. El producto podría estar duplicado o contener valores incorrectos.';
    }

    if (error.status === 401 || error.status === 403) {
      return 'Tu sesión no permite registrar productos.';
    }

    return 'No fue posible registrar el producto.';
  }
}