import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { environment } from '../../../environments/environment';
import { CategoryResponse } from '../models/category-response';
import { CategoryService } from './category.service';

describe('CategoryService', () => {
  let service: CategoryService;
  let httpTesting: HttpTestingController;

  const categories: CategoryResponse[] = [
    {
      id: 1,
      name: 'TRIPLES',
      active: true,
      displayOrder: 1,
    },
    {
      id: 2,
      name: 'SANGUCHITOS',
      active: true,
      displayOrder: 2,
    },
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(CategoryService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should retrieve all active categories', () => {
    service.findAll().subscribe((response) => {
      expect(response).toEqual(categories);
    });

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/categories`,
    );

    expect(request.request.method).toBe('GET');

    request.flush(categories);
  });
});
