import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { LoginRequest } from '../models/login-request';
import { LoginResponse } from '../models/login-response';

interface StoredSession {
  response: LoginResponse;
  expiresAt: number;
}

const SESSION_STORAGE_KEY = 'aurea.auth.session';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly sessionState = signal<LoginResponse | null>(
    this.restoreSession(),
  );

  readonly session = this.sessionState.asReadonly();

  readonly isAuthenticated = computed(
    () => this.sessionState() !== null,
  );

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(
        `${environment.apiUrl}/auth/login`,
        request,
      )
      .pipe(
        tap((response) => {
          this.saveSession(response);
        }),
      );
  }

  logout(): void {
    sessionStorage.removeItem(SESSION_STORAGE_KEY);
    this.sessionState.set(null);
  }

  getToken(): string | null {
    return this.sessionState()?.token ?? null;
  }

  private saveSession(response: LoginResponse): void {
    const storedSession: StoredSession = {
      response,
      expiresAt: Date.now() + response.expiresIn * 1000,
    };

    sessionStorage.setItem(
      SESSION_STORAGE_KEY,
      JSON.stringify(storedSession),
    );

    this.sessionState.set(response);
  }

  private restoreSession(): LoginResponse | null {
    if (typeof sessionStorage === 'undefined') {
      return null;
    }

    const storedValue = sessionStorage.getItem(
      SESSION_STORAGE_KEY,
    );

    if (!storedValue) {
      return null;
    }

    try {
      const storedSession = JSON.parse(
        storedValue,
      ) as StoredSession;

      if (
        !storedSession.response?.token ||
        storedSession.expiresAt <= Date.now()
      ) {
        sessionStorage.removeItem(SESSION_STORAGE_KEY);
        return null;
      }

      return storedSession.response;
    } catch {
      sessionStorage.removeItem(SESSION_STORAGE_KEY);
      return null;
    }
  }
}
