# 📱 Application de Suivi d'Habitudes (Habit Tracker)

Une application Android moderne développée avec **Jetpack Compose** permettant de suivre, gérer et renforcer ses habitudes quotidiennes de manière ludique avec un système de statistiques style RPG (Vie, Expérience, MP).

---

## 📸 Aperçu de l'interface

![Capture d'écran de l'application](screenshots/app_preview.png)

> *Remarque : La capture d'écran montre l'interface principale avec la carte de profil personnalisée et la liste des habitudes.*

---

## ✨ Fonctionnalités principales

- **Profil & Statistiques dynamiques :**
  - Barres de progression interactives : **VIE**, **EXP** (Expérience) et **MP**.
  - Mise à jour automatique des statistiques lors de la validation ou du retrait d'une habitude.
  - **Personnalisation de la photo de profil :** Choix d'une image depuis la galerie avec sauvegarde permanente dans le stockage local de l'appareil (ne disparaît pas à la fermeture de l'application).

- **Gestion des habitudes :**
  - **Incrémentation / Décrémentation (+ / -) :** Cliquez sur `+` pour valider une habitude (gagne de l'EXP/VIE) ou sur `-` pour indiquer un manquement.
  - **Badges de compteurs :** Affichage en temps réel des actions réalisées sur chaque habitude (ex: `+2 / -1`).
  - **Ajout d'habitudes personnalisées :** Créez de nouvelles habitudes avec le bouton floating `+ Nouvelle`.
  - **Suppression d'habitude :** Effectuez un **appui long (Long Click)** sur n'importe quelle carte d'habitude pour la supprimer.
  - **Réinitialisation globale :** Réinitialisez les compteurs et jauges à tout moment via le bouton `Réinitialiser`.

- **Persistance des données :**
  - Toutes les habitudes, statistiques et le chemin de la photo de profil sont sauvegardés localement via **Jetpack DataStore** et le stockage interne sécurisé de l'application.

---

## 🛠️ Stack Technique

- **Langage :** Kotlin
- **UI Framework :** Jetpack Compose (Material 3)
- **Architecture :** MVVM (Model-View-ViewModel)
- **Gestion d'état :** `StateFlow` & Coroutines
- **Persistance :** Jetpack DataStore (Preferences) + Gson
- **Chargement d'images :** Coil (Compose)

---

## 🚀 Installation & Utilisation de l'APK

### Option 1 : Installer le fichier APK directement sur Android
1. Téléchargez le fichier `.apk` situé dans le dossier `app/build/outputs/apk/debug/` (ou depuis la section *Releases* du projet).
2. Transférez le fichier `.apk` sur votre téléphone Android.
3. Autorisez l'installation depuis des sources inconnues dans les paramètres de votre téléphone si demandé.
4. Ouvrez le fichier pour démarrer l'installation puis lancez l'application.

### Option 2 : Exécuter depuis Android Studio
1. Clonez le dépôt Git :
   ```bash
   git clone [https://github.com/votre-nom-utilisateur/nom-du-depot.git](https://github.com/votre-nom-utilisateur/nom-du-depot.git)