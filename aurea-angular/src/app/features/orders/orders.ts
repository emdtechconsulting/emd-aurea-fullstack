import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
} from '@angular/forms';
import { RouterLink } from '@angular/router';

import { OrderResponse } from '../../core/models/order-response';
import { OrderSource } from '../../core/models/order-source';
import { OrderStatus } from '../../core/models/order-status';
import { OrderService } from '../../core/services/order.service';

@Component({
  selector: 'app-orders',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './orders.html',
  styleUrl: './orders.scss',
})
export class Orders implements OnInit {
  private readonly orderService = inject(OrderService);
  private readonly formBuilder = inject(FormBuilder);

  private readonly currencyFormatter =
    new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN',
    });

  readonly orders = signal<OrderResponse[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal('');

  readonly page = signal(0);
  readonly size = signal(10);
  readonly totalElements = signal(0);
  readonly totalPages = signal(0);
  readonly first = signal(true);
  readonly last = signal(true);

  readonly statuses: OrderStatus[] = [
    'DRAFT',
    'CONFIRMED',
    'PREPARING',
    'DELIVERED',
    'CANCELLED',
  ];

  readonly sources: OrderSource[] = [
    'WEB',
    'MOBILE',
    'FIREBASE_MIGRATION',
    'MANUAL',
  ];

  readonly filterForm = this.formBuilder.nonNullable.group({
    status: ['' as OrderStatus | ''],
    source: ['' as OrderSource | ''],
    customer: [''],
    size: [10],
  });

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(targetPage = 0): void {
    const filters = this.filterForm.getRawValue();

    this.loading.set(true);
    this.errorMessage.set('');

    this.orderService.findAll({
      status: filters.status || undefined,
      source: filters.source || undefined,
      customer: filters.customer,
      page: targetPage,
      size: filters.size,
    }).subscribe({
      next: (response) => {
        this.orders.set(response.content);
        this.page.set(response.page);
        this.size.set(response.size);
        this.totalElements.set(response.totalElements);
        this.totalPages.set(response.totalPages);
        this.first.set(response.first);
        this.last.set(response.last);
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

  applyFilters(): void {
    this.loadOrders(0);
  }

  clearFilters(): void {
    this.filterForm.reset({
      status: '',
      source: '',
      customer: '',
      size: 10,
    });

    this.loadOrders(0);
  }

  previousPage(): void {
    if (!this.first()) {
      this.loadOrders(this.page() - 1);
    }
  }

  nextPage(): void {
    if (!this.last()) {
      this.loadOrders(this.page() + 1);
    }
  }

  statusLabel(status: OrderStatus): string {
    const labels: Record<OrderStatus, string> = {
      DRAFT: 'Borrador',
      CONFIRMED: 'Confirmado',
      PREPARING: 'En preparaciÃ³n',
      DELIVERED: 'Entregado',
      CANCELLED: 'Cancelado',
    };

    return labels[status];
  }

  sourceLabel(source: OrderSource): string {
    const labels: Record<OrderSource, string> = {
      WEB: 'Web',
      MOBILE: 'MÃ³vil',
      FIREBASE_MIGRATION: 'MigraciÃ³n Firebase',
      MANUAL: 'Manual',
    };

    return labels[source];
  }

  formatMoney(value: number): string {
    return this.currencyFormatter.format(value);
  }

  formatTime(value: string | null): string {
    return value?.slice(0, 5) || 'Sin hora';
  }

  private resolveErrorMessage(
    error: HttpErrorResponse,
  ): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor de AUREA.';
    }

    if (error.status === 401 || error.status === 403) {
      return 'Tu sesiÃ³n no permite consultar los pedidos.';
    }

    if (error.status === 400) {
      return 'Los filtros o datos de paginaciÃ³n no son vÃ¡lidos.';
    }

    return 'No fue posible cargar los pedidos.';
  }
}