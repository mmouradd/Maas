export interface Passager {
  id?: number;
  nom: string;
  prenom: string;
  numeroVol: string;
  dateVol: string;        // ISO date string
  nationalite?: string;
  numeroPasport?: string;
}
