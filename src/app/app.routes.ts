import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'unauthorized',
    loadComponent: () =>
      import('./features/auth/unauthorized/unauthorized.component').then(m => m.UnauthorizedComponent),
  },

  // ─── Espace Agence ───────────────────────────────────────────────
  {
    path: 'agence',
    canActivate: [authGuard, roleGuard(['AGENCE'])],
    loadComponent: () =>
      import('./features/agence/agence-shell/agence-shell.component').then(m => m.AgenceShellComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/agence/dashboard/dashboard.component').then(m => m.AgenceDashboardComponent),
      },
      {
        path: 'nouvelle-demande',
        loadComponent: () =>
          import('./features/agence/nouvelle-demande/nouvelle-demande.component').then(m => m.NouvellDemandeComponent),
      },
      {
        path: 'historique',
        loadComponent: () =>
          import('./features/agence/historique/historique.component').then(m => m.HistoriqueComponent),
      },
      {
        path: 'demande/:id',
        loadComponent: () =>
          import('./features/agence/detail-demande/detail-demande.component').then(m => m.AgenceDetailDemandeComponent),
      },
    ],
  },

  // ─── Espace Gestionnaire GSRM ────────────────────────────────────
  {
    path: 'gsrm',
    canActivate: [authGuard, roleGuard(['GSRM'])],
    loadComponent: () =>
      import('./features/gsrm/gsrm-shell/gsrm-shell.component').then(m => m.GsrmShellComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/gsrm/dashboard/dashboard.component').then(m => m.GsrmDashboardComponent),
      },
      {
        path: 'demandes',
        loadComponent: () =>
          import('./features/gsrm/demandes/demandes.component').then(m => m.DemandesComponent),
      },
      {
        path: 'demande/:id',
        loadComponent: () =>
          import('./features/gsrm/detail-demande/detail-demande.component').then(m => m.GsrmDetailDemandeComponent),
      },
    ],
  },

  // ─── Espace Escale ───────────────────────────────────────────────
  {
    path: 'escale',
    canActivate: [authGuard, roleGuard(['ESCALE'])],
    loadComponent: () =>
      import('./features/escale/escale-shell/escale-shell.component').then(m => m.EscaleShellComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/escale/dashboard/dashboard.component').then(m => m.EscaleDashboardComponent),
      },
      {
        path: 'demande/:id',
        loadComponent: () =>
          import('./features/escale/detail-demande/detail-demande.component').then(m => m.EscaleDetailDemandeComponent),
      },
    ],
  },

  // ─── Espace Administration ───────────────────────────────────────
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard(['ADMIN'])],
    loadComponent: () =>
      import('./features/admin/admin-shell/admin-shell.component').then(m => m.AdminShellComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/admin/dashboard/dashboard.component').then(m => m.AdminDashboardComponent),
      },
      {
        path: 'utilisateurs',
        loadComponent: () =>
          import('./features/admin/utilisateurs/utilisateurs.component').then(m => m.UtilisateursComponent),
      },
      {
        path: 'agences',
        loadComponent: () =>
          import('./features/admin/agences/agences.component').then(m => m.AgencesComponent),
      },
    ],
  },

  // ─── Redirect par défaut ─────────────────────────────────────────
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' },
];
