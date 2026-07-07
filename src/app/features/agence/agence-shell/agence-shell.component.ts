import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-agence-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './agence-shell.component.html',
  styleUrl: './agence-shell.component.css',
})
export class AgenceShellComponent {
  private auth  = inject(AuthService);
  private notif = inject(NotificationService);

  readonly user   = this.auth.currentUser;
  readonly unread = this.notif.unreadCount;

  constructor() {
    this.notif.loadAll().subscribe();
  }

  logout(): void { this.auth.logout(); }
}
