import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormArray, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { DemandeService } from '../../../core/services/demande.service';
import { ESCALES } from '../../../core/models';

@Component({
  selector: 'app-nouvelle-demande',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './nouvelle-demande.component.html',
})
export class NouvellDemandeComponent {
  private fb     = inject(FormBuilder);
  private svc    = inject(DemandeService);
  private router = inject(Router);

  readonly ESCALES = ESCALES;
  readonly loading = signal(false);
  readonly error   = signal<string | null>(null);

  form = this.fb.group({
    typeService:       ['ARRIVAL', Validators.required],
    escaleId:          [null as number | null, Validators.required],
    dateService:       ['', Validators.required],
    nomGreeter:        [''],
    coutService:       [0, [Validators.required, Validators.min(0)]],
    fraisAdditionnels: [0, [Validators.required, Validators.min(0)]],
    passagers: this.fb.array([this.newPassager()]),
  });

  get passagers() { return this.form.get('passagers') as FormArray; }

  get coutTotal(): number {
    return (this.form.value.coutService ?? 0) + (this.form.value.fraisAdditionnels ?? 0);
  }

  newPassager() {
    return this.fb.group({
      nom:         ['', Validators.required],
      prenom:      ['', Validators.required],
      numeroVol:   ['', Validators.required],
      dateVol:     ['', Validators.required],
      nationalite: [''],
    });
  }

  addPassager(): void { this.passagers.push(this.newPassager()); }
  removePassager(i: number): void { if (this.passagers.length > 1) this.passagers.removeAt(i); }

  submit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    const val = this.form.value as any;
    this.svc.create({
      typeService:       val.typeService,
      escaleId:          Number(val.escaleId),
      dateService:       val.dateService,
      nomGreeter:        val.nomGreeter,
      coutService:       val.coutService,
      fraisAdditionnels: val.fraisAdditionnels,
      nombrePassagers:   val.passagers.length,
      passagers:         val.passagers,
    }).subscribe({
      next: d   => this.router.navigate(['/agence/demande', d.id]),
      error: err => {
        this.error.set(err?.error?.message ?? 'Erreur lors de la creation.');
        this.loading.set(false);
      },
    });
  }
}
