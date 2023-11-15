# Alfresco Content Extractor
## Table des matières
- [À propos](#à-propos)
- [Prérequis](#prérequis)
- [Utilisation](#utilisation)
- [Contributeurs](#contributeurs)

## À propos
L'application Java "Alfresco Content Extractor" permet d’extraire tout ou partie du contenu situé dans un répertoire (repository) Alfresco. Le répertoire sera reproduit sur le PC ou le serveur qui exécute l’application. 

Toute la structure des fichiers et des dossiers sera reproduite à l’identique, et leurs propriétés/métadonnées enregistrées dans des fichiers JSON qui seront insérés au même emplacement que ces fichiers/dossiers. 

## Prérequis
- Java version 11 ou postérieure
- Environnement de développement pour Java (Eclipse, IntelliJ IDEA, ...)
- Instance d'Alfresco version  ou postérieure en cours de fonctionnement

## Utilisation
1. Cloner le projet
2. Ouvrir le projet avec votre IDE
4. Ouvrir le fichier extractor_application.properties
5. Renseigner le champ destinationDirectory avec le chemin absolu vers le dossier où seront créés les dossiers et fichiers issus de l'extraction. Attention : si le dossier de destination n'est pas vide au lancement du programme, celui-ci sera vidé.
6. Renseigner le champ serviceUrl avec l'adresse URL de l'instance Alfresco (si déployée en local : http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser)
7. Renseigner le champ selectiveExtractPath avec le chemin du répertoire Alfresco à extraire (conserver / pour extraire la totalité)
8. Enregistrer le fichier extractor_application.properties
9. Builder le projet
10. Dans la console, renseigner le login et le mot de passe utilisés par l'administrateur pour se connecter à Alfresco
11. La connexion établie, appuyer sur la touche [Y] pour lancer l'extraction

## Contributeurs
LANGOWSKI Lucas (https://github.com/SNK215)

LECOEUR Alexandre (https://github.com/LecoeurAlexandre)

