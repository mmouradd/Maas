import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-gsrm-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="shell">
      <nav class="sidebar">
        <div class="logo">✈️ MAAS</div>
        <div class="role">Gestionnaire GSRM</div>
        <ul>
          <li><a routerLink="dashboard" routerLinkActive="active">🏠 Dashboard</a></li>
          <li><a routerLink="demandes" routerLinkActive="active">📋 Toutes les demandes</a></li>
        </ul>
        <button class="logout" (click)="auth.logout()">Déconnexion</button>
      </nav>
      <main class="content"><router-outlet /></main>
    </div>
  `,
  styleUrl: '../../../features/agence/agence-shell/agence-shell.component.css',
})
export class GsrmShellComponent {
  constructor(public auth: AuthService) {}
}
