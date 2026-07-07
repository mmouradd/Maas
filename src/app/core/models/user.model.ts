export type UserRole = 'AGENCE' | 'GSRM' | 'ESCALE' | 'ADMIN';

export interface User {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: UserRole;
  agenceId?: number;   // si role === 'AGENCE'
  escaleId?: number;   // si role === 'ESCALE'
  actif: boolean;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  motDePasse: string;
}
