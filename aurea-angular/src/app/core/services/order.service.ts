import {
  HttpClient,
  HttpParams,
} from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { OrderFilters } from '../models/order-filters';
import { PagedOrderResponse } from '../models/paged-order-response';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/orders`;

  findAll(
    filters: OrderFilters = {},
  ): Observable<PagedOrderResponse> {
    let params = new HttpParams()
      .set('page', filters.page ?? 0)
      .set('size', filters.size ?? 10);

    if (filters.status) {
      params = params.set(
        'status',
        filters.status,
      );
    }

    if (filters.source) {
      params = params.set(
        'source',
        filters.source,
      );
    }

    const customer = filters.customer?.trim();

    if (customer) {
      params = params.set(
        'customer',
        customer,
      );
    }

    return this.http.get<PagedOrderResponse>(
      this.endpoint,
      {
        params,
      },
    );
  }
}