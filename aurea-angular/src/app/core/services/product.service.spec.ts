import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { environment } from '../../../environments/environment';
import { ProductRequest } from '../models/product-request';
import { ProductResponse } from '../models/product-response';
import { ProductService } from './product.service';

describe('ProductService', () => {
  let service: ProductService;
  let httpTesting: HttpTestingController;

  const product: ProductResponse = {
    id: 1,
    categoryId: 1,
    categoryName: 'TRIPLES',
    name: 'Triple clásico',
    description: 'Producto de prueba',
    active: true,
    prices: [
      {
        id: 1,
        quantity: 25,
        price: 50,
        active: true,
      },
    ],
    createdAt: '2026-09-14T00:00:00',
    updatedAt: '2026-09-14T00:00:00',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(ProductService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should retrieve all products', () => {
    service.findAll().subscribe((products) => {
      expect(products).toEqual([product]);
    });

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/products`,
    );

    expect(request.request.method).toBe('GET');

    request.flush([product]);
  });

  it('should create a product', () => {
    const productRequest: ProductRequest = {
      categoryId: 1,
      name: 'Triple clásico',
      description: 'Producto de prueba',
      active: true,
      prices: [
        {
          quantity: 25,
          price: 50,
        },
      ],
    };

    service.create(productRequest).subscribe((createdProduct) => {
      expect(createdProduct).toEqual(product);
    });

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/products`,
    );

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(productRequest);

    request.flush(product);
  });
});
