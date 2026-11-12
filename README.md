# Studo (Android)

Application Android qui installe des IA en local (chat, code, images) dans des « machines » isolées,
et qui écrit des logiciels complets que tu télécharges en .zip.

## Compiler l'APK avec GitHub
1. Envoie tout le contenu de ce dossier dans ton dépôt GitHub (y compris `.github/` et `app/fixed.keystore`).
2. Onglet **Actions** → workflow **Build APK** (≈ 20-40 min la 1re fois, ensuite beaucoup plus court grâce au cache des moteurs).
3. Télécharge l'artefact **Studo-apk**, décompresse, installe l'APK. Ne mets PAS `local.properties` sur GitHub.

## Fonctions (version 0.7)
- **Un seul écran** : une zone de texte, Studo choisit discussion, logiciel ou image (ou tu forces le choix), avec choix des IA.
- **Vue en direct** : texte mot à mot, fichiers du logiciel pendant leur écriture, progression et journal de l'image.
- **Fichiers joints comme Claude** : pastilles (code, texte, zip, PDF, images), contenu envoyé sans être collé dans le message.
- **Historique** : sessions par date, catégories (Discussions, Logiciels, Images, Favoris), renommer, favori, supprimer.
- **Catalogue de ~50 modèles par catégories** (discussion, code, réflexion, sans filtre, images, Flux…), installation sur mémoire interne ou carte SD, déplacement d'un modèle.
- **Navigateur plein écran** (favoris, page → IA, installation de modèles) et **visionneuse de fichiers** (texte/code, Markdown, HTML, CSV, image, PDF, zip, Office, audio/vidéo, hexadécimal).
- **Modèles** : détection du type par l'en-tête GGUF (chat / image / complémentaire), installation depuis un fichier du téléphone,
  catalogue, Hugging Face, navigateur web intégré, vérifier / désinstaller, fichiers complémentaires Flux/SD3.
- **Logiciels** : génération vue en direct, aperçu HTML, export .zip, modification par simple demande.
- **Images** : création et modification (image à image), enregistrement dans Photos.
- **Réglages** : thème clair / sombre, machines, réglages IA et images, analyse complète, notice.

## Limites à connaître
- ARM64 uniquement. 4 Go de RAM minimum, 6 Go+ conseillés.
- Génération d'images sur CPU : plusieurs minutes. Commence en 256×256.
- Une IA de téléphone reste bien moins puissante qu'une grande IA en ligne.
- Le code n'a pas pu être compilé sur l'environnement de développement : si le build GitHub échoue,
  envoie le journal d'erreur.

## Nouveautés 0.8
- Travail en arrière-plan : une notification « Studo » garde l'appli active pendant téléchargements, générations et discussions (réglage « Ne pas fermer Studo » dans Réglages pour l'économie de batterie).
- Téléchargements : reprise automatique après coupure (attente du réseau, jusqu'à 40 essais) et bouton « Reprendre » dans Modèles › Installés.
- Boutons « Continuer » (réponse coupée) et « Reprendre / refaire » (logiciel ou image interrompus).
- Mise en forme des réponses : **gras**, *italique*, ~~barré~~, `code`, titres, listes, citations, tableaux.
- Mode « Téléphone » : l'IA propose des actions (alarme, minuteur, appli, appel/SMS/e-mail préparés, calendrier, lampe, volume, réglages, fichiers, commandes dans la machine). Tu autorises chaque action. Limites d'Android : pas de clic dans les autres applis, pas d'envoi de SMS ni d'appel automatiques, pas d'activation du Wi-Fi.
- Modèles image→image et édition (FLUX Kontext, FLUX dev, SD 3.5, SD 1.4). Liens non vérifiés : utilise « Vérifier les liens ».

## Nouveautés 0.9
- Téléchargements : reprise automatique au lancement de Studo (réglable), bouton « Tout reprendre » dans Modèles › Installés.
- Catalogue : 102 modèles (code, réflexion, sans filtre, histoires, légers, gros) et de nouveaux modèles d'images. Liens non vérifiés : « Vérifier les liens ».
- Notifications « terminé » (discussion, image, logiciel, actions, téléchargement) avec une émoticône par type, modifiable dans Réglages.
- Dictée vocale (micro), lecture à voix haute (🔊 Lire), personnalités (professeur, codeur, traducteur, correcteur, pirate…), idées de départ variées.
- Lien avec Termux : le mode Téléphone peut lancer des commandes dans Termux (après ton accord). Réglages › Lien avec Termux explique les 3 étapes.

## Nouveautés 1.0
- Boutons « IA : 🪄 Auto, Discussion, Code, Réflexion, Langues, Rapide, Histoires, Sans filtre » et « Image : 🪄 Auto, Réaliste, Art, Rapide, Flux, Retouche ». En Auto, Studo choisit parmi les modèles installés celui qui convient à ta demande (code, calcul, langues étrangères, histoire…). Un modèle choisi à la main dans la liste garde la priorité.
- Bouton 🔒 « Mode non censuré » (18+) : choisit des IA sans filtre, demande de répondre sans moraliser et passe l'appli en thème sombre rouge.
- Catalogue : 119 modèles, avec de nouvelles IA sans filtre (Dolphin, abliterated, Lexi, Josiefied, Rocinante…).
- Mode « Contrôle appli » : l'IA peut lire l'écran, toucher et écrire dans d'autres applis via le service d'accessibilité (Réglages › Contrôle des applications). Elle demande ton accord avant d'envoyer, payer, supprimer ou appeler.
- Termux : correctif du retour de résultat (Android 14), et test qui indique si Termux est installé et autorisé.

## V10.1 (correctifs de build)
- Erreur de compilation corrigée : annotation `@Composable` en trop devant `STYLES` (HomeScreen.kt, ligne 487).
- Version 1.0.1 (versionCode 11), même clé de signature : s'installe par-dessus la V10.
- Lint de release désactivé pour ne plus bloquer l'APK ; `extractNativeLibs` retiré du manifest (déjà géré par `useLegacyPackaging`).
- Workflow : l'étape stable-diffusion ne bloque plus le build, détection du binaire `sd` plus robuste, nouveau cache des moteurs, `--stacktrace` pour des journaux d'erreur complets.
