export type CodeEscale = 'MUC' | 'DUS' | 'FRA' | 'HAM' | 'STR';

export interface Escale {
  id: number;
  code: CodeEscale;
  nom: string;
  ville: string;
  pays: string;
}

export const ESCALES: Escale[] = [
  { id: 1, code: 'MUC', nom: 'Munich', ville: 'Munich', pays: 'Allemagne' },
  { id: 2, code: 'DUS', nom: 'Düsseldorf', ville: 'Düsseldorf', pays: 'Allemagne' },
  { id: 3, code: 'FRA', nom: 'Francfort', ville: 'Francfort', pays: 'Allemagne' },
  { id: 4, code: 'HAM', nom: 'Hambourg', ville: 'Hambourg', pays: 'Allemagne' },
  { id: 5, code: 'STR', nom: 'Stuttgart', ville: 'Stuttgart', pays: 'Allemagne' },
];
