import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DemandeService } from '../../../core/services/demande.service';
import { Demande, STATUT_LABELS, STATUT_COLORS } from '../../../core/models';
import { ByStatutPipe } from '../../../shared/pipes/by-statut.pipe';

@Component({
  selector: 'app-agence-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, ByStatutPipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class AgenceDashboardComponent implements OnInit {
  readonly demandes = signal<Demande[]>([]);
  readonly loading  = signal(true);

  readonly STATUT_LABELS = STATUT_LABELS;
  readonly STATUT_COLORS = STATUT_COLORS;

  constructor(private demandeService: DemandeService) {}

  ngOnInit(): void {
    this.demandeService.getAll({ limit: 10 }).subscribe({
      next: res => { this.demandes.set(res.data); this.loading.set(false); },
      error: ()  => this.loading.set(false),
    });
  }
}
