# Rendez-vous geolocalise

## Description

> Énoncé disponible [ici](https://www.labri.fr/perso/zemmari/pam/Projet2017.pdf)

Le projet a pour but le développement d’une application permettant d’organiser des rendez-vous géolocalisés.

__Le fonctionnement de l'application est le suivant :__
- Au lancement, l’utilisateur doit pouvoir soit choisir un (ou 	plusieurs) contact(s), soit saisir le numéro de la personne à contacter (la (ou les) personne(s) contactée(s) doit	 avoir également installé l’application).
- L’application	récupère alors les coordonnées GPS actuelles ou permet à l'utilisateur dans choisir d'autres et les envoie à la ou les personnes choisies.
- À la réception de l'invitation, la personne peut soit accepter soit refuser le rendez-vous. La personne ayant initié le rendez-vous est alors avertie de la décision de l'invité(e).

## Télécharger l'APK

L'application signée est généré lors de la release : [rdv-geo.apk](https://github.com/adrien-chinour/rendez-vous-geolocalise/releases/latest/download/rdv-geo.apk)

## Développement

### Conventions

1. Le code est a écrire en anglais
2. Les commentaires et la documentation sont en français

### Installation du projet

```bash
git clone https://github.com/adrien-chinour/rendez-vous-geolocalise
```

Ajouter le fichier `secrets.xml` dans le répertoire `app/src/mail/res/values/` avec le contenu suivant :

```xml
<resources>
    <!-- Clé d'API Google -->
    <string name="google_api_key">API_KEY</string>
</resources>
```

Remplacer `API_KEY` avec votre clé API Google autorisant l'accès à l'API **Maps SDK for Android** et **Time Zone API**.

**Le projet est prêt !**
