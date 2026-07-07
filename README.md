# Backend — Application Web de Gestion des Services MAAS (GSRM)

API REST Spring Boot 3 / MySQL implémentant le cahier des charges v1.0 (juillet 2026) : gestion des demandes Meet & Assist Service pour les escales MUC, DUS, FRA, HAM et STR.

## Stack technique

| Composant | Choix |
|---|---|
| Langage / Framework | Java 17, Spring Boot 3.3 |
| Base de données | MySQL 8 (Spring Data JPA / Hibernate) |
| Authentification | JWT (jjwt), stateless |
| Mots de passe | BCrypt |
| Contrôle d'accès | RBAC — rôles `AGENCE`, `GESTIONNAIRE`, `ESCALE`, `ADMIN` |
| Export | Excel (Apache POI) et PDF (OpenPDF) |
| Emails | Spring Mail (SMTP), envoi asynchrone, désactivable |

## Démarrage rapide

1. **Prérequis** : JDK 17+, Maven 3.9+, MySQL 8 en local (port 3306).
2. **Configuration** : ajuster `src/main/resources/application.properties`
   (identifiants MySQL, `app.jwt.secret` — 32 caractères minimum, `app.admin.password`).
   La base `maas_db` est créée automatiquement (`createDatabaseIfNotExist=true`),
   et Hibernate crée les tables (`ddl-auto=update`). Le script `schema.sql`
   est fourni séparément à titre de référence (section 8 du cahier des charges).
3. **Lancer** (le Maven wrapper est inclus, Maven n'a pas besoin d'être installé) :
   ```bash
   ./mvnw spring-boot:run        # Linux / macOS
   mvnw.cmd spring-boot:run      # Windows
   ```
   Tests unitaires : `./mvnw test`.
   Au premier démarrage, l'application initialise les 5 escales (MUC, DUS, FRA, HAM, STR)
   et un compte administrateur (`admin@gsrm.local` / mot de passe de `application.properties` —
   à changer immédiatement).

## Authentification

```
POST /api/auth/login
{ "email": "admin@gsrm.local", "motDePasse": "ChangeMe123!" }
```

La réponse contient le token JWT à passer ensuite dans chaque requête :
`Authorization: Bearer <token>`.

Les comptes utilisateurs sont créés par l'administrateur via `POST /api/admin/utilisateurs`.

## Endpoints principaux

### Demandes MAAS (`/api/demandes`)
| Méthode | Chemin | Description |
|---|---|---|
| POST | `/api/demandes` | Créer une demande (agence, gestionnaire ou admin). Numéro généré automatiquement (`MAAS-FRA-2026-XXXXX`), coût total calculé, notification automatique du gestionnaire et de l'escale, passage au statut « Notifiée ». |
| GET | `/api/demandes` | Liste filtrable : `?idEscale=&idAgence=&statut=&typeService=&dateDebut=&dateFin=`. Cloisonnement automatique : une agence ne voit que ses demandes, une escale que les siennes. |
| GET | `/api/demandes/{id}` | Détail d'une demande (passagers inclus). |
| GET | `/api/demandes/{id}/historique` | Historique immuable des statuts (traçabilité). |
| PUT | `/api/demandes/{id}` | Modifier une demande non finalisée. |
| PATCH | `/api/demandes/{id}/statut` | Changer le statut : `{ "nouveauStatut": "REFUSEE", "motif": "..." }`. |

### Cycle de vie et droits sur les statuts
Transitions contrôlées : NOUVELLE → NOTIFIEE → EN_COURS → CONFIRMEE → TRAITEE, avec REFUSEE et ANNULEE comme sorties. Règles appliquées :
- motif **obligatoire** pour `REFUSEE` et `ANNULEE` ;
- seule l'escale concernée (ou le gestionnaire/admin) peut confirmer, refuser ou traiter ;
- une agence ne peut qu'annuler ses propres demandes ;
- chaque changement notifie l'agence émettrice (in-app + email) et est journalisé dans `historique_statuts` (insert-only).

### Notifications (`/api/notifications`)
`GET /api/notifications?nonLues=true`, `GET /api/notifications/count`, `PATCH /api/notifications/{id}/lu`.

### Référentiels
`GET /api/escales`, `GET /api/agences` (lecture pour les formulaires).

### Administration (`/api/admin` — rôle ADMIN)
Création/liste/désactivation des utilisateurs, CRUD agences et escales.

### Gestionnaire GSRM
- `GET /api/dashboard/stats` : total, répartition par statut / escale / agence, demandes en attente.
- `GET /api/export/demandes/excel` et `GET /api/export/demandes/pdf` (mêmes filtres que la liste).

## Matrice des rôles

| Action | AGENCE | ESCALE | GESTIONNAIRE | ADMIN |
|---|---|---|---|---|
| Créer une demande | ✅ (sa propre agence) | ❌ | ✅ | ✅ |
| Voir les demandes | Ses demandes | Son escale | Toutes | Toutes |
| Confirmer / refuser / traiter | ❌ | ✅ (son escale) | ✅ | ✅ |
| Annuler | ✅ (ses demandes) | ❌ | ✅ | ✅ |
| Dashboard & exports | ❌ | ❌ | ✅ | ✅ |
| Administration | ❌ | ❌ | ❌ | ✅ |

## Sécurité (section 9.2 du cahier des charges)

- Authentification obligatoire sur tous les endpoints (sauf `/api/auth/login` et `/api/health`).
- RBAC via Spring Security + vérifications métier au niveau service.
- Mots de passe hachés avec BCrypt.
- HTTPS : à activer au niveau du reverse proxy / hébergement (hors périmètre du code).
- Journalisation : historique des statuts immuable en base.

## Structure du projet

```
src/main/java/com/gsrm/maas
├── entity/       Utilisateur, Agence, Escale, DemandeMaas, Passager, Notification, HistoriqueStatut + enums
├── repository/   Spring Data JPA
├── dto/          Requêtes/réponses validées (Bean Validation)
├── security/     JWT, filtre, UserDetailsService, SecurityConfig
├── service/      Règles de gestion, notifications, exports, dashboard
├── controller/   API REST
├── exception/    Gestion centralisée des erreurs (404 / 403 / 422 / 400)
└── config/       Initialisation des données (escales + admin)
```

## Tester l'API

Une collection Postman prête à l'emploi est fournie : `postman_collection.json`
(importer dans Postman → lancer « Login admin » : le token JWT est enregistré
automatiquement pour toutes les autres requêtes, dans l'ordre des dossiers 1 à 6).
