import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserRole } from '../models';

export const roleGuard = (allowedRoles: UserRole[]): CanActivateFn => {
  return (route: ActivatedRouteSnapshot) => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const user = auth.currentUser();

    if (user && allowedRoles.includes(user.role)) {
      return true;
    }
    return router.createUrlTree(['/unauthorized']);
  };
};
