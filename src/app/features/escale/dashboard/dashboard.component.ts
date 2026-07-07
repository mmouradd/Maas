import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DemandeService } from '../../../core/services/demande.service';
import { AuthService } from '../../../core/services/auth.service';
import { Demande, STATUT_LABELS, STATUT_COLORS } from '../../../core/models';

@Component({
  selector: 'app-escale-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <h2 style="font-size:20px;font-weight:700;margin-bottom:20px">Mes demandes</h2>
    @if (loading()) { <p>Chargement...</p> }
    @else {
      <p>{{ demandes().length }} demande(s) pour cette escale</p>
      @for (d of demandes(); track d.id) {
        <div style="background:#fff;border-radius:10px;padding:16px;margin-bottom:12px;box-shadow:0 1px 4px rgba(0,0,0,.06)">
          <div style="display:flex;justify-content:space-between;align-items:center">
            <strong>{{ d.reference }}</strong>
            <span style="border-radius:20px;padding:3px 10px;font-size:11px;font-weight:600;color:#fff" [style.background]="STATUT_COLORS[d.statut]">
              {{ STATUT_LABELS[d.statut] }}
            </span>
          </div>
          <div style="font-size:13px;color:#666;margin-top:6px">{{ d.typeService }} · {{ d.dateService | date:'dd/MM/yyyy HH:mm' }} · {{ d.nombrePassagers }} pax</div>
          <a [routerLink]="'../demande/' + d.id" style="display:inline-block;margin-top:10px;color:#34A853;font-weight:600;text-decoration:none;font-size:13px">Traiter →</a>
        </div>
      }
    }
  `,
})
export class EscaleDashboardComponent implements OnInit {
  readonly demandes = signal<Demande[]>([]);
  readonly loading  = signal(true);
  readonly STATUT_LABELS = STATUT_LABELS;
  readonly STATUT_COLORS = STATUT_COLORS;
  constructor(private svc: DemandeService, private auth: AuthService) {}
  ngOnInit() {
    const escaleId = this.auth.currentUser()?.escaleId;
    this.svc.getAll({ escaleId }).subscribe({
      next: r => { this.demandes.set(r.data); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
