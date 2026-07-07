export type TypeNotification = 'STATUT_CHANGE' | 'NOUVELLE_DEMANDE' | 'COMMENTAIRE';

export interface Notification {
  id: number;
  demandeId: number;
  demandeReference?: string;
  type: TypeNotification;
  message: string;
  lu: boolean;
  createdAt: string;
}
