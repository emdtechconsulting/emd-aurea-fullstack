import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { OrderFilters } from '../../core/models/order-filters';
import { OrderResponse } from '../../core/models/order-response';
import { PagedOrderResponse } from '../../core/models/paged-order-response';
import { OrderService } from '../../core/services/order.service';
import { Orders } from './orders';

const order: OrderResponse = {
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
};

class OrderServiceStub {
  readonly requests: OrderFilters[] = [];

  findAll(
    filters: OrderFilters = {},
  ) {
    this.requests.push(filters);

    const page = filters.page ?? 0;

    const response: PagedOrderResponse = {
      content: [order],
      page,
      size: filters.size ?? 10,
      totalElements: 21,
      totalPages: 3,
      first: page === 0,
      last: page === 2,
    };

    return of(response);
  }
}

describe('Orders', () => {
  let fixture: ComponentFixture<Orders>;
  let component: Orders;
  let orderService: OrderServiceStub;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Orders],
      providers: [
        provideRouter([]),
        {
          provide: OrderService,
          useClass: OrderServiceStub,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Orders);
    component = fixture.componentInstance;

    orderService = TestBed.inject(
      OrderService,
    ) as unknown as OrderServiceStub;

    fixture.detectChanges();
  });

  it('should load the first page of orders', () => {
    expect(orderService.requests).toEqual([
      {
        status: undefined,
        source: undefined,
        customer: '',
        page: 0,
        size: 10,
      },
    ]);

    expect(component.orders()).toEqual([order]);
    expect(component.page()).toBe(0);
    expect(component.totalElements()).toBe(21);
    expect(component.totalPages()).toBe(3);
    expect(component.loading()).toBe(false);

    expect(
      component.statusLabel('CONFIRMED'),
    ).toBe('Confirmado');

    expect(
      component.sourceLabel('WEB'),
    ).toBe('Web');

    expect(
      component.formatTime('15:30:00'),
    ).toBe('15:30');
  });

  it('should apply status, source and customer filters', () => {
    component.filterForm.setValue({
      status: 'CONFIRMED',
      source: 'WEB',
      customer: 'María',
      size: 20,
    });

    component.applyFilters();

    expect(
      orderService.requests.at(-1),
    ).toEqual({
      status: 'CONFIRMED',
      source: 'WEB',
      customer: 'María',
      page: 0,
      size: 20,
    });

    expect(component.page()).toBe(0);
    expect(component.size()).toBe(20);
  });

  it('should navigate between order pages', () => {
    component.nextPage();

    expect(
      orderService.requests.at(-1)?.page,
    ).toBe(1);

    expect(component.page()).toBe(1);
    expect(component.first()).toBe(false);
    expect(component.last()).toBe(false);

    component.previousPage();

    expect(
      orderService.requests.at(-1)?.page,
    ).toBe(0);

    expect(component.page()).toBe(0);
    expect(component.first()).toBe(true);
  });
});