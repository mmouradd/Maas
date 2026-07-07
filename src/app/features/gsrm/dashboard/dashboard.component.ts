import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DemandeService } from '../../../core/services/demande.service';
import { Demande, STATUT_LABELS, STATUT_COLORS } from '../../../core/models';

@Component({
  selector: 'app-gsrm-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <h2 style="font-size:20px;font-weight:700;margin-bottom:20px">Supervision GSRM</h2>
    @if (loading()) { <p>Chargement...</p> }
    @else {
      <p style="color:#666;margin-bottom:16px">{{ demandes().length }} demandes trouvées</p>
      <a routerLink="../demandes" style="background:#F5A623;color:#fff;padding:10px 20px;border-radius:8px;text-decoration:none;font-weight:600">
        Voir toutes les demandes →
      </a>
    }
  `,
})
export class GsrmDashboardComponent implements OnInit {
  readonly demandes = signal<Demande[]>([]);
  readonly loading  = signal(true);
  constructor(private svc: DemandeService) {}
  ngOnInit() {
    this.svc.getAll().subscribe({ next: r => { this.demandes.set(r.data); this.loading.set(false); }, error: () => this.loading.set(false) });
  }
}
