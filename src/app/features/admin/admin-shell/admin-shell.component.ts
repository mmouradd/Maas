import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-admin-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div style="display:flex;min-height:100vh">
      <nav style="width:220px;background:#2a1a40;color:#fff;padding:24px 0;display:flex;flex-direction:column">
        <div style="padding:0 20px 4px;font-size:20px;font-weight:700">✈️ MAAS</div>
        <div style="padding:0 20px 24px;font-size:11px;color:#c0a0e8;text-transform:uppercase">Administration</div>
        <ul style="list-style:none;padding:0;margin:0;flex:1">
          <li><a routerLink="dashboard"    routerLinkActive="active" style="display:block;padding:12px 20px;color:#d0c0e8;text-decoration:none">🏠 Dashboard</a></li>
          <li><a routerLink="utilisateurs" routerLinkActive="active" style="display:block;padding:12px 20px;color:#d0c0e8;text-decoration:none">👤 Utilisateurs</a></li>
          <li><a routerLink="agences"      routerLinkActive="active" style="display:block;padding:12px 20px;color:#d0c0e8;text-decoration:none">🏢 Agences</a></li>
        </ul>
        <div style="padding:20px;border-top:1px solid #3d2a60">
          <button (click)="auth.logout()" style="width:100%;padding:8px;background:transparent;border:1px solid #5d4080;color:#d0c0e8;border-radius:6px;cursor:pointer">Déconnexion</button>
        </div>
      </nav>
      <main style="flex:1;background:#f5f7fb;padding:24px"><router-outlet /></main>
    </div>
  `,
})
export class AdminShellComponent {
  constructor(public auth: AuthService) {}
}
