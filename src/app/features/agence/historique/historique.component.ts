import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-historique',
  standalone: true,
  imports: [CommonModule],
  template: `<h2 style="font-size:20px;font-weight:700">Historique des demandes</h2>
             <p style="color:#666;margin-top:8px">A implementer : liste complete avec filtres (escale, date, statut).</p>`,
})
export class HistoriqueComponent {}
