import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  template: `
    <div style="text-align:center; padding:80px 20px;">
      <h1>🚫 Accès non autorisé</h1>
      <p style="color:#666; margin: 16px 0;">Vous n'avez pas les droits pour accéder à cette page.</p>
      <button (click)="goBack()" style="padding:10px 24px; background:#4C8BF5; color:#fff; border:none; border-radius:8px; cursor:pointer; font-size:14px;">
        Retourner à mon espace
      </button>
    </div>
  `,
})
export class UnauthorizedComponent {
  constructor(private auth: AuthService) {}
  goBack(): void { this.auth.redirectByRole(); }
}
