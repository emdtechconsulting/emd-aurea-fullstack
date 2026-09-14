import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ProductRequest } from '../models/product-request';
import { ProductResponse } from '../models/product-response';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/products`;

  findAll(): Observable<ProductResponse[]> {
    return this.http.get<ProductResponse[]>(
      this.endpoint,
    );
  }

  create(
    request: ProductRequest,
  ): Observable<ProductResponse> {
    return this.http.post<ProductResponse>(
      this.endpoint,
      request,
    );
  }
}
