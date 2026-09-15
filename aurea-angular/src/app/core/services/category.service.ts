import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { CategoryResponse } from '../models/category-response';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/categories`;

  findAll(): Observable<CategoryResponse[]> {
    return this.http.get<CategoryResponse[]>(
      this.endpoint,
    );
  }
}
