#! /bin/bash

# Permet de générer le fichier secrets.xml pour la CI
envsubst < ./.ci/secrets.xml.exemple > app/src/main/res/values/secrets.xml