import { Component, OnInit, signal, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DemandeService } from '../../../core/services/demande.service';
import { Demande, StatutDemande, STATUT_LABELS, STATUT_COLORS } from '../../../core/models';

@Component({
  selector: 'app-gsrm-detail-demande',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h2 style="font-size:20px;font-weight:700;margin-bottom:20px">Détail demande — Vue GSRM</h2>
    @if (demande()) {
      <div style="background:#fff;border-radius:10px;padding:24px;box-shadow:0 1px 4px rgba(0,0,0,.06)">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px">
          <div style="font-family:monospace;font-size:18px;font-weight:700">{{ demande()!.reference }}</div>
          <span style="border-radius:20px;padding:6px 14px;font-size:13px;font-weight:600;color:#fff" [style.background]="STATUT_COLORS[demande()!.statut]">
            {{ STATUT_LABELS[demande()!.statut] }}
          </span>
        </div>
        <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:16px;font-size:13px">
          <div><strong>Agence</strong><br>{{ demande()!.agenceNom }}</div>
          <div><strong>Escale</strong><br>{{ demande()!.escaleCode }}</div>
          <div><strong>Type</strong><br>{{ demande()!.typeService }}</div>
          <div><strong>Date service</strong><br>{{ demande()!.dateService | date:'dd/MM/yyyy HH:mm' }}</div>
          <div><strong>Coût total</strong><br><strong>{{ demande()!.coutTotal | currency:'EUR' }}</strong></div>
        </div>
        <p style="margin-top:20px;color:#888;font-size:13px">Actions de validation/refus à implémenter ici.</p>
      </div>
    }
  `,
})
export class GsrmDetailDemandeComponent implements OnInit {
  readonly id = input.required<string>();
  readonly demande = signal<Demande | null>(null);
  readonly STATUT_LABELS = STATUT_LABELS;
  readonly STATUT_COLORS = STATUT_COLORS;
  constructor(private svc: DemandeService) {}
  ngOnInit() {
    this.svc.getById(Number(this.id())).subscribe(d => this.demande.set(d));
  }
}
