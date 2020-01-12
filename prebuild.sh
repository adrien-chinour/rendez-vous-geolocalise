#! /bin/bash

# Permet de générer le fichier secrets.xml pour la CI
envsubst < .ci/secrets.exemple.xml > app/src/main/res/values/secrets.xml