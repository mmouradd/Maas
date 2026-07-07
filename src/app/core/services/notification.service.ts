import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Notification } from '../models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  readonly notifications = signal<Notification[]>([]);
  readonly unreadCount   = signal<number>(0);

  constructor(private http: HttpClient) {}

  loadAll() {
    return this.http
      .get<Notification[]>(`${environment.apiUrl}/notifications`)
      .pipe(
        tap(list => {
          this.notifications.set(list);
          this.unreadCount.set(list.filter(n => !n.lu).length);
        })
      );
  }

  markAsRead(id: number) {
    return this.http
      .patch<Notification>(`${environment.apiUrl}/notifications/${id}/lu`, {})
      .pipe(
        tap(() => {
          this.notifications.update(list =>
            list.map(n => (n.id === id ? { ...n, lu: true } : n))
          );
          this.unreadCount.update(c => Math.max(0, c - 1));
        })
      );
  }
}
