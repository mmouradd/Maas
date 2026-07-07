import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

type UserRole = 'AGENCE' | 'GSRM' | 'ESCALE' | 'ADMIN';

@Component({
  selector: 'app-dev-toolbar',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dev-bar">
      <span class="dev-label">DEV</span>
      <span class="dev-current">Role : <strong>{{ currentRole() }}</strong></span>
      <button *ngFor="let r of roles" (click)="switchRole(r)" [class.active]="r === currentRole()">{{ r }}</button>
    </div>
  `,
  styles: [`
    .dev-bar {
      position: fixed; bottom: 0; left: 0; right: 0; z-index: 9999;
      background: #1a1a2e; color: #fff; padding: 8px 16px;
      display: flex; align-items: center; gap: 10px; font-size: 12px;
    }
    .dev-label { background: #e53935; color: #fff; padding: 2px 8px; border-radius: 4px; font-weight: 700; }
    .dev-current { color: #aaa; margin-right: 8px; }
    button { padding: 4px 12px; border: 1px solid #444; background: #2d2d4e; color: #ccc; border-radius: 6px; cursor: pointer; font-size: 12px; }
    button:hover { background: #3d3d6e; color: #fff; }
    button.active { background: #4C8BF5; color: #fff; border-color: #4C8BF5; }
  `]
})
export class DevToolbarComponent {
  private router = inject(Router);
  readonly roles: UserRole[] = ['AGENCE', 'GSRM', 'ESCALE', 'ADMIN'];

  currentRole(): string {
    try { return JSON.parse(localStorage.getItem('maas_user') || '{}').role ?? '?'; }
    catch { return '?'; }
  }

  switchRole(role: UserRole): void {
    const routes: Record<UserRole, string> = {
      AGENCE: '/agence/dashboard', GSRM: '/gsrm/dashboard',
      ESCALE: '/escale/dashboard', ADMIN: '/admin/dashboard',
    };
    localStorage.setItem('maas_token', 'dev-fake-token');
    localStorage.setItem('maas_user', JSON.stringify({
      id: 1, nom: 'Boulaares', prenom: 'Mohamed Al Amine',
      email: 'dev@maas.tn', role, agenceId: 1, escaleId: 1, actif: true,
    }));
    this.router.navigate([routes[role]]);
  }
}
