import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DemandeService } from '../../../core/services/demande.service';
import { Demande, FiltresDemandes, StatutDemande, STATUT_LABELS, STATUT_COLORS, ESCALES } from '../../../core/models';

@Component({
  selector: 'app-demandes',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './demandes.component.html',
})
export class DemandesComponent implements OnInit {
  readonly demandes = signal<Demande[]>([]);
  readonly loading  = signal(true);
  readonly STATUT_LABELS = STATUT_LABELS;
  readonly STATUT_COLORS = STATUT_COLORS;
  readonly ESCALES = ESCALES;

  filtres: FiltresDemandes = {};

  constructor(private svc: DemandeService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.svc.getAll(this.filtres).subscribe({
      next: r => { this.demandes.set(r.data); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  exportExcel() {
    this.svc.exportExcel(this.filtres).subscribe(blob => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a'); a.href = url; a.download = 'demandes.xlsx'; a.click();
    });
  }
}
