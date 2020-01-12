#! /bin/bash

# Permet de générer le fichier secrets.xml pour la CI
envsubst < app/src/main/res/values/secrets.xml.exemple > app/src/main/res/values/secrets.xml