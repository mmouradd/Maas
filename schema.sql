-- =====================================================================
-- Application Web de Gestion des Services MAAS (GSRM)
-- Script de création des tables — MySQL 8+
-- NB : avec spring.jpa.hibernate.ddl-auto=update, Hibernate crée ces
-- tables automatiquement. Ce script est fourni à titre de référence
-- (cahier des charges, section 8) et pour une création manuelle.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS maas_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE maas_db;

CREATE TABLE IF NOT EXISTS agences (
    id_agence       INT AUTO_INCREMENT PRIMARY KEY,
    nom_agence      VARCHAR(255) NOT NULL,
    code_agence     VARCHAR(50)  NOT NULL UNIQUE,
    email_contact   VARCHAR(255),
    telephone       VARCHAR(50),
    actif           BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS escales (
    id_escale       INT AUTO_INCREMENT PRIMARY KEY,
    nom_escale      VARCHAR(255) NOT NULL,
    code_iata       VARCHAR(3)   NOT NULL UNIQUE,
    email_contact   VARCHAR(255),
    actif           BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS utilisateurs (
    id_utilisateur  INT AUTO_INCREMENT PRIMARY KEY,
    nom             VARCHAR(255) NOT NULL,
    prenom          VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe    VARCHAR(255) NOT NULL,
    role            ENUM('AGENCE','GESTIONNAIRE','ESCALE','ADMIN') NOT NULL,
    id_agence       INT NULL,
    id_escale       INT NULL,
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_utilisateur_agence FOREIGN KEY (id_agence) REFERENCES agences(id_agence),
    CONSTRAINT fk_utilisateur_escale FOREIGN KEY (id_escale) REFERENCES escales(id_escale)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS demandes_maas (
    id_demande             INT AUTO_INCREMENT PRIMARY KEY,
    numero_demande         VARCHAR(50) NOT NULL UNIQUE,
    id_agence              INT NOT NULL,
    id_escale              INT NOT NULL,
    type_service           ENUM('ARRIVAL','DEPARTURE','TRANSIT') NOT NULL,
    date_service           DATE NOT NULL,
    heure_service          TIME NOT NULL,
    nombre_passagers       INT NOT NULL,
    cout_service           DECIMAL(10,2) NOT NULL,
    frais_additionnels     DECIMAL(10,2) NOT NULL DEFAULT 0,
    cout_total             DECIMAL(10,2) NOT NULL,
    nom_greeter            VARCHAR(255),
    statut                 ENUM('NOUVELLE','NOTIFIEE','EN_COURS','CONFIRMEE','REFUSEE','TRAITEE','ANNULEE') NOT NULL DEFAULT 'NOUVELLE',
    motif_refus_annulation VARCHAR(500),
    cree_par               INT NOT NULL,
    date_creation          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_maj               DATETIME,
    CONSTRAINT fk_demande_agence FOREIGN KEY (id_agence) REFERENCES agences(id_agence),
    CONSTRAINT fk_demande_escale FOREIGN KEY (id_escale) REFERENCES escales(id_escale),
    CONSTRAINT fk_demande_createur FOREIGN KEY (cree_par) REFERENCES utilisateurs(id_utilisateur)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS passagers (
    id_passager     INT AUTO_INCREMENT PRIMARY KEY,
    id_demande      INT NOT NULL,
    nom             VARCHAR(255) NOT NULL,
    prenom          VARCHAR(255) NOT NULL,
    numero_vol      VARCHAR(50),
    numero_document VARCHAR(100),
    CONSTRAINT fk_passager_demande FOREIGN KEY (id_demande) REFERENCES demandes_maas(id_demande) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notifications (
    id_notification   INT AUTO_INCREMENT PRIMARY KEY,
    id_demande        INT NOT NULL,
    id_destinataire   INT NOT NULL,
    type_notification VARCHAR(100) NOT NULL,
    message           VARCHAR(500) NOT NULL,
    lu                BOOLEAN NOT NULL DEFAULT FALSE,
    date_envoi        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_demande      FOREIGN KEY (id_demande)      REFERENCES demandes_maas(id_demande),
    CONSTRAINT fk_notification_destinataire FOREIGN KEY (id_destinataire) REFERENCES utilisateurs(id_utilisateur)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS historique_statuts (
    id_historique     INT AUTO_INCREMENT PRIMARY KEY,
    id_demande        INT NOT NULL,
    statut_precedent  VARCHAR(50),
    nouveau_statut    VARCHAR(50) NOT NULL,
    modifie_par       INT NOT NULL,
    commentaire       VARCHAR(500),
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historique_demande FOREIGN KEY (id_demande)  REFERENCES demandes_maas(id_demande),
    CONSTRAINT fk_historique_auteur  FOREIGN KEY (modifie_par) REFERENCES utilisateurs(id_utilisateur)
) ENGINE=InnoDB;

-- Escales du périmètre (cahier des charges, annexe 11.2)
INSERT IGNORE INTO escales (nom_escale, code_iata) VALUES
  ('Aéroport de Munich', 'MUC'),
  ('Aéroport de Düsseldorf', 'DUS'),
  ('Aéroport de Francfort', 'FRA'),
  ('Aéroport de Hambourg', 'HAM'),
  ('Aéroport de Stuttgart', 'STR');
