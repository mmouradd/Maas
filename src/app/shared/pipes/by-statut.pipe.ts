import { Pipe, PipeTransform } from '@angular/core';
import { Demande, StatutDemande } from '../../core/models';

@Pipe({ name: 'byStatut', standalone: true })
export class ByStatutPipe implements PipeTransform {
  transform(demandes: Demande[], statut: StatutDemande): number {
    return demandes.filter(d => d.statut === statut).length;
  }
}
