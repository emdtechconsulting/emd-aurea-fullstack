import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  provideRouter,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';

import { AuthService } from '../services/auth.service';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  let authenticated: boolean;
  let router: Router;

  beforeEach(() => {
    authenticated = false;

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            isAuthenticated: () => authenticated,
          },
        },
      ],
    });

    router = TestBed.inject(Router);
  });

  function executeGuard(url: string) {
    return TestBed.runInInjectionContext(() =>
      authGuard(
        {} as ActivatedRouteSnapshot,
        { url } as RouterStateSnapshot,
      ),
    );
  }

  it('should allow access when the user is authenticated', () => {
    authenticated = true;

    expect(executeGuard('/dashboard')).toBe(true);
  });

  it('should redirect unauthenticated users to login', () => {
    const result = executeGuard('/dashboard') as UrlTree;

    expect(router.serializeUrl(result)).toBe(
      '/login?returnUrl=%2Fdashboard',
    );
  });
});
