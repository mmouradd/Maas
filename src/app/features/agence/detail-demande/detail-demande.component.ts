import { Component, OnInit, signal, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DemandeService } from '../../../core/services/demande.service';
import { Demande, STATUT_LABELS, STATUT_COLORS } from '../../../core/models';

@Component({
  selector: 'app-detail-demande-agence',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h2 style="font-size:20px;font-weight:700;margin-bottom:20px">Détail de la demande</h2>
    @if (loading()) { <p>Chargement...</p> }
    @else if (demande()) {
      <div style="background:#fff;border-radius:10px;padding:24px;box-shadow:0 1px 4px rgba(0,0,0,.06)">
        <div style="display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:20px">
          <div>
            <div style="font-size:11px;color:#888;text-transform:uppercase;margin-bottom:4px">Référence</div>
            <div style="font-family:monospace;font-size:16px;font-weight:700">{{ demande()!.reference }}</div>
          </div>
          <span style="border-radius:20px;padding:6px 14px;font-size:13px;font-weight:600;color:#fff" [style.background]="STATUT_COLORS[demande()!.statut]">
            {{ STATUT_LABELS[demande()!.statut] }}
          </span>
        </div>
        <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:16px;font-size:13px">
          <div><strong>Type</strong><br>{{ demande()!.typeService }}</div>
          <div><strong>Escale</strong><br>{{ demande()!.escaleCode }}</div>
          <div><strong>Date service</strong><br>{{ demande()!.dateService | date:'dd/MM/yyyy HH:mm' }}</div>
          <div><strong>Passagers</strong><br>{{ demande()!.nombrePassagers }}</div>
          <div><strong>Coût service</strong><br>{{ demande()!.coutService | currency:'EUR' }}</div>
          <div><strong>Coût total</strong><br><strong>{{ demande()!.coutTotal | currency:'EUR' }}</strong></div>
        </div>
      </div>
    }
  `,
})
export class AgenceDetailDemandeComponent implements OnInit {
  readonly id = input.required<string>();
  readonly demande = signal<Demande | null>(null);
  readonly loading = signal(true);
  readonly STATUT_LABELS = STATUT_LABELS;
  readonly STATUT_COLORS = STATUT_COLORS;
  constructor(private svc: DemandeService) {}
  ngOnInit() {
    this.svc.getById(Number(this.id())).subscribe({
      next: d => { this.demande.set(d); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
