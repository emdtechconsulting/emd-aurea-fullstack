import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal('');

  protected readonly loginForm =
    this.formBuilder.nonNullable.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });

  protected submit(): void {
    this.errorMessage.set('');

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.submitting.set(true);

    this.authService
      .login(this.loginForm.getRawValue())
      .subscribe({
        next: () => {
          void this.router.navigateByUrl('/dashboard');
        },
        error: (error: HttpErrorResponse) => {
          this.submitting.set(false);

          if (error.status === 401) {
            this.errorMessage.set(
              'Usuario o contraseña incorrectos.',
            );
            return;
          }

          if (error.status === 0) {
            this.errorMessage.set(
              'No fue posible conectar con el servidor.',
            );
            return;
          }

          this.errorMessage.set(
            'Ocurrió un error. Inténtalo nuevamente.',
          );
        },
        complete: () => {
          this.submitting.set(false);
        },
      });
  }
}
