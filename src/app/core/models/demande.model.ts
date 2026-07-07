import { Passager } from './passager.model';

export type TypeService = 'ARRIVAL' | 'DEPARTURE' | 'TRANSIT';

export type StatutDemande =
  | 'NOUVELLE'
  | 'ENVOYEE'
  | 'EN_EXAMEN'
  | 'CONFIRMEE'
  | 'EN_COURS'
  | 'TRAITEE'
  | 'REFUSEE'
  | 'ANNULEE';

export interface Demande {
  id: number;
  reference: string;           // ex: MAAS-2026-00042
  typeService: TypeService;
  escaleId: number;
  escaleCode?: string;
  agenceId: number;
  agenceNom?: string;
  dateService: string;         // ISO datetime string
  nombrePassagers: number;
  passagers: Passager[];
  nomGreeter?: string;
  coutService: number;
  fraisAdditionnels: number;
  coutTotal: number;           // calculé auto : coutService + fraisAdditionnels
  statut: StatutDemande;
  motifRefusAnnulation?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateDemandeRequest {
  typeService: TypeService;
  escaleId: number;
  dateService: string;
  nombrePassagers: number;
  passagers: Passager[];
  nomGreeter?: string;
  coutService: number;
  fraisAdditionnels: number;
}

export interface FiltresDemandes {
  escaleId?: number;
  agenceId?: number;
  statut?: StatutDemande;
  typeService?: TypeService;
  dateDebut?: string;
  dateFin?: string;
  page?: number;
  limit?: number;
}

export const STATUT_LABELS: Record<StatutDemande, string> = {
  NOUVELLE:   'Nouvelle',
  ENVOYEE:    'Envoyée',
  EN_EXAMEN:  'En examen',
  CONFIRMEE:  'Confirmée',
  EN_COURS:   'En cours',
  TRAITEE:    'Traitée',
  REFUSEE:    'Refusée',
  ANNULEE:    'Annulée',
};

export const STATUT_COLORS: Record<StatutDemande, string> = {
  NOUVELLE:   '#9B59B6',
  ENVOYEE:    '#4C8BF5',
  EN_EXAMEN:  '#F5A623',
  CONFIRMEE:  '#34A853',
  EN_COURS:   '#1AAEDB',
  TRAITEE:    '#2E7D32',
  REFUSEE:    '#E53935',
  ANNULEE:    '#757575',
};
