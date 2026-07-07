import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Demande,
  CreateDemandeRequest,
  FiltresDemandes,
  StatutDemande,
  HistoriqueStatut,
} from '../models';
import { environment } from '../../../environments/environment';

interface PagedResult<T> {
  data: T[];
  total: number;
  page: number;
  limit: number;
}

@Injectable({ providedIn: 'root' })
export class DemandeService {
  private readonly base = `${environment.apiUrl}/demandes`;

  constructor(private http: HttpClient) {}

  getAll(filtres: FiltresDemandes = {}): Observable<PagedResult<Demande>> {
    let params = new HttpParams();
    Object.entries(filtres).forEach(([key, val]) => {
      if (val !== undefined && val !== null) params = params.set(key, String(val));
    });
    return this.http.get<PagedResult<Demande>>(this.base, { params });
  }

  getById(id: number): Observable<Demande> {
    return this.http.get<Demande>(`${this.base}/${id}`);
  }

  create(data: CreateDemandeRequest): Observable<Demande> {
    return this.http.post<Demande>(this.base, data);
  }

  updateStatut(
    id: number,
    statut: StatutDemande,
    motif?: string
  ): Observable<Demande> {
    return this.http.patch<Demande>(`${this.base}/${id}/statut`, { statut, motif });
  }

  getHistorique(id: number): Observable<HistoriqueStatut[]> {
    return this.http.get<HistoriqueStatut[]>(`${this.base}/${id}/historique`);
  }

  exportExcel(filtres: FiltresDemandes = {}): Observable<Blob> {
    let params = new HttpParams();
    Object.entries(filtres).forEach(([key, val]) => {
      if (val !== undefined && val !== null) params = params.set(key, String(val));
    });
    return this.http.get(`${this.base}/export/excel`, {
      params,
      responseType: 'blob',
    });
  }

  exportPdf(filtres: FiltresDemandes = {}): Observable<Blob> {
    let params = new HttpParams();
    Object.entries(filtres).forEach(([key, val]) => {
      if (val !== undefined && val !== null) params = params.set(key, String(val));
    });
    return this.http.get(`${this.base}/export/pdf`, {
      params,
      responseType: 'blob',
    });
  }
}
