import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';

// DEV : token initial pour passer les guards au premier chargement
if (!localStorage.getItem('maas_token')) {
  localStorage.setItem('maas_token', 'dev-fake-token');
  localStorage.setItem('maas_user', JSON.stringify({
    id: 1, nom: 'Boulaares', prenom: 'Mohamed Al Amine',
    email: 'dev@maas.tn', role: 'AGENCE', agenceId: 1, escaleId: 1, actif: true,
  }));
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([jwtInterceptor])),
  ],
};
