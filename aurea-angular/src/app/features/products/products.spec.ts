import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import { of } from 'rxjs';

import { CategoryResponse } from '../../core/models/category-response';
import { ProductRequest } from '../../core/models/product-request';
import { ProductResponse } from '../../core/models/product-response';
import { CategoryService } from '../../core/services/category.service';
import { ProductService } from '../../core/services/product.service';
import { Products } from './products';

const category: CategoryResponse = {
  id: 6,
  name: 'OTROS',
  active: true,
  displayOrder: 6,
};

const existingProduct: ProductResponse = {
  id: 1,
  categoryId: 1,
  categoryName: 'TRIPLES',
  name: 'Triple clásico',
  description: 'Producto existente',
  active: true,
  prices: [
    {
      id: 1,
      quantity: 25,
      price: 50,
      active: true,
    },
  ],
  createdAt: '2026-09-15T00:00:00',
  updatedAt: '2026-09-15T00:00:00',
};

const createdProduct: ProductResponse = {
  id: 22,
  categoryId: 6,
  categoryName: 'OTROS',
  name: 'Producto prueba',
  description: null,
  active: true,
  prices: [
    {
      id: 58,
      quantity: 25,
      price: 25.5,
      active: true,
    },
    {
      id: 59,
      quantity: 50,
      price: 48,
      active: true,
    },
    {
      id: 60,
      quantity: 100,
      price: 90,
      active: true,
    },
  ],
  createdAt: '2026-09-15T00:00:00',
  updatedAt: '2026-09-15T00:00:00',
};

class ProductServiceStub {
  readonly requests: ProductRequest[] = [];

  findAll() {
    return of([existingProduct]);
  }

  create(request: ProductRequest) {
    this.requests.push(request);

    return of(createdProduct);
  }
}

class CategoryServiceStub {
  findAll() {
    return of([category]);
  }
}

describe('Products', () => {
  let fixture: ComponentFixture<Products>;
  let component: Products;
  let productService: ProductServiceStub;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Products],
      providers: [
        {
          provide: ProductService,
          useClass: ProductServiceStub,
        },
        {
          provide: CategoryService,
          useClass: CategoryServiceStub,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Products);
    component = fixture.componentInstance;

    productService = TestBed.inject(
      ProductService,
    ) as unknown as ProductServiceStub;

    fixture.detectChanges();
  });

  it('should load products and categories', () => {
    expect(component.products()).toEqual([
      existingProduct,
    ]);

    expect(component.categories()).toEqual([
      category,
    ]);

    expect(
      component.productForm.controls.categoryId.value,
    ).toBe(category.id);

    expect(component.loading()).toBe(false);
    expect(component.categoriesLoading()).toBe(false);
  });

  it('should create a valid product', () => {
    component.formVisible.set(true);

    component.productForm.setValue({
      categoryId: category.id,
      name: '  Producto prueba  ',
      description: '   ',
      active: true,
      price25: 25.5,
      price50: 48,
      price100: 90,
    });

    component.saveProduct();

    expect(productService.requests).toEqual([
      {
        categoryId: 6,
        name: 'Producto prueba',
        description: null,
        active: true,
        prices: [
          {
            quantity: 25,
            price: 25.5,
          },
          {
            quantity: 50,
            price: 48,
          },
          {
            quantity: 100,
            price: 90,
          },
        ],
      },
    ]);

    expect(component.products()).toEqual([
      existingProduct,
      createdProduct,
    ]);

    expect(component.successMessage()).toContain(
      'registrado correctamente',
    );

    expect(component.formVisible()).toBe(false);
    expect(component.saving()).toBe(false);
  });

  it('should not submit an invalid product', () => {
    component.productForm.reset({
      categoryId: 0,
      name: '',
      description: '',
      active: true,
      price25: 0,
      price50: 0,
      price100: 0,
    });

    component.saveProduct();

    expect(component.productForm.invalid).toBe(true);
    expect(productService.requests).toEqual([]);
    expect(component.saving()).toBe(false);
  });
});