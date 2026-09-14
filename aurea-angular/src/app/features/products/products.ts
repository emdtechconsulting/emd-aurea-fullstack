import {
  Component,
  computed,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { ProductResponse } from '../../core/models/product-response';
import { ProductService } from '../../core/services/product.service';

@Component({
  selector: 'app-products',
  templateUrl: './products.html',
  styleUrl: './products.scss',
})
export class Products implements OnInit {
  private readonly productService = inject(ProductService);

  readonly products = signal<ProductResponse[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal('');

  readonly activeProducts = computed(
    () =>
      this.products().filter(
        (product) => product.active,
      ).length,
  );

  ngOnInit(): void {
    this.loadProducts();
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
          this.resolveErrorMessage(error),
        );
        this.loading.set(false);
      },
    });
  }

  private resolveErrorMessage(
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
}
