import { TestBed } from '@angular/core/testing';
import {
  HttpHandlerFn,
  HttpRequest,
  HttpResponse,
} from '@angular/common/http';
import { of } from 'rxjs';

import { AuthService } from '../services/auth.service';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  let token: string | null;
  let capturedRequest: HttpRequest<unknown> | undefined;

  const next: HttpHandlerFn = (request) => {
    capturedRequest = request;

    return of(
      new HttpResponse({
        status: 200,
      }),
    );
  };

  beforeEach(() => {
    token = null;
    capturedRequest = undefined;

    TestBed.configureTestingModule({
      providers: [
        {
          provide: AuthService,
          useValue: {
            getToken: () => token,
          },
        },
      ],
    });
  });

  function execute(request: HttpRequest<unknown>): void {
    TestBed.runInInjectionContext(() =>
      authInterceptor(request, next),
    ).subscribe();
  }

  it('should add the Bearer token to protected requests', () => {
    token = 'test-jwt-token';

    execute(
      new HttpRequest(
        'GET',
        'http://127.0.0.1:8080/api/products',
      ),
    );

    expect(
      capturedRequest?.headers.get('Authorization'),
    ).toBe('Bearer test-jwt-token');
  });

  it('should not add a token when there is no session', () => {
    execute(
      new HttpRequest(
        'GET',
        'http://127.0.0.1:8080/api/products',
      ),
    );

    expect(
      capturedRequest?.headers.has('Authorization'),
    ).toBe(false);
  });

  it('should not add a token to the login request', () => {
    token = 'test-jwt-token';

    execute(
      new HttpRequest(
        'POST',
        'http://127.0.0.1:8080/api/auth/login',
        null,
      ),
    );

    expect(
      capturedRequest?.headers.has('Authorization'),
    ).toBe(false);
  });
});
