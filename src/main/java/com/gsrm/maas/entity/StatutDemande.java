package com.gsrm.maas.entity;

public enum StatutDemande {
    NOUVELLE("Nouvelle"),
    NOTIFIEE("Notifiée"),
    EN_COURS("En cours de traitement"),
    CONFIRMEE("Confirmée"),
    REFUSEE("Refusée"),
    TRAITEE("Traitée"),
    ANNULEE("Annulée");

    private final String libelle;

    StatutDemande(String libelle) { this.libelle = libelle; }

    public String getLibelle() { return libelle; }
}
