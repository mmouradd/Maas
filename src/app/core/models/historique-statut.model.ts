import { StatutDemande } from './demande.model';

export interface HistoriqueStatut {
  id: number;
  demandeId: number;
  statutAvant: StatutDemande;
  statutApres: StatutDemande;
  commentaire?: string;
  createdBy: number;        // userId
  createdByNom?: string;
  createdAt: string;
}
