import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private fb   = inject(FormBuilder);
  private auth = inject(AuthService);

  readonly loading = signal(false);
  readonly error   = signal<string | null>(null);

  form = this.fb.group({
    email:      ['', [Validators.required, Validators.email]],
    motDePasse: ['', Validators.required],
  });

  submit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.error.set(null);
    const { email, motDePasse } = this.form.value as { email: string; motDePasse: string };
    this.auth.login({ email, motDePasse }).subscribe({
      next: () => this.auth.redirectByRole(),
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Identifiants incorrects.');
        this.loading.set(false);
      },
    });
  }
}
