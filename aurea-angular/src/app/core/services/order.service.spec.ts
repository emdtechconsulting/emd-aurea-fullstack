import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { environment } from '../../../environments/environment';
import { PagedOrderResponse } from '../models/paged-order-response';
import { OrderService } from './order.service';

describe('OrderService', () => {
  let service: OrderService;
  let httpTesting: HttpTestingController;

  const response: PagedOrderResponse = {
    content: [
      {
        id: 10,
        customerName: 'María Pérez',
        deliveryDate: '2026-09-20',
        deliveryTime: '15:30:00',
        requiresDelivery: true,
        district: 'Chorrillos',
        deliveryFee: 10,
        productsSubtotal: 90,
        total: 100,
        status: 'CONFIRMED',
        source: 'WEB',
      },
    ],
    page: 0,
    size: 10,
    totalElements: 1,
    totalPages: 1,
    first: true,
    last: true,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(OrderService);
    httpTesting = TestBed.inject(
      HttpTestingController,
    );
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should retrieve the first order page', () => {
    service.findAll().subscribe((result) => {
      expect(result).toEqual(response);
    });

    const request = httpTesting.expectOne(
      (candidate) =>
        candidate.url ===
          `${environment.apiUrl}/orders` &&
        candidate.params.get('page') === '0' &&
        candidate.params.get('size') === '10',
    );

    expect(request.request.method).toBe('GET');
    expect(request.request.params.has('status')).toBe(false);
    expect(request.request.params.has('source')).toBe(false);
    expect(request.request.params.has('customer')).toBe(false);

    request.flush(response);
  });

  it('should send filters and pagination', () => {
    service.findAll({
      status: 'CONFIRMED',
      source: 'WEB',
      customer: '  María  ',
      page: 2,
      size: 20,
    }).subscribe((result) => {
      expect(result).toEqual(response);
    });

    const request = httpTesting.expectOne(
      (candidate) =>
        candidate.url ===
        `${environment.apiUrl}/orders`,
    );

    expect(request.request.method).toBe('GET');
    expect(request.request.params.get('status')).toBe(
      'CONFIRMED',
    );
    expect(request.request.params.get('source')).toBe(
      'WEB',
    );
    expect(request.request.params.get('customer')).toBe(
      'María',
    );
    expect(request.request.params.get('page')).toBe('2');
    expect(request.request.params.get('size')).toBe('20');

    request.flush(response);
  });
});