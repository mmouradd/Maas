import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { User, AuthResponse, LoginRequest } from '../models';
import { environment } from '../../../environments/environment';

const TOKEN_KEY = 'maas_token';
const USER_KEY  = 'maas_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly _user = signal<User | null>(this.loadUser());

  readonly currentUser = this._user.asReadonly();
  readonly isLoggedIn  = computed(() => this._user() !== null);
  readonly userRole    = computed(() => this._user()?.role ?? null);

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: LoginRequest) {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, credentials)
      .pipe(
        tap(res => {
          localStorage.setItem(TOKEN_KEY, res.token);
          localStorage.setItem(USER_KEY, JSON.stringify(res.user));
          this._user.set(res.user);
        })
      );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this._user.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  /** Redirige vers l'espace correspondant au rôle de l'utilisateur */
  redirectByRole(): void {
    const role = this._user()?.role;
    const routes: Record<string, string> = {
      AGENCE: '/agence',
      GSRM:   '/gsrm',
      ESCALE: '/escale',
      ADMIN:  '/admin',
    };
    if (role) this.router.navigate([routes[role] ?? '/login']);
  }

  private loadUser(): User | null {
    try {
      const raw = localStorage.getItem(USER_KEY);
      return raw ? (JSON.parse(raw) as User) : null;
    } catch {
      return null;
    }
  }
}
