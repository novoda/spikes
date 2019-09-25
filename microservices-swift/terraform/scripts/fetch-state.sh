#! /usr/bin/env bash

# This script fetches previously persisted terraform state metadata (see publish-state.sh)
# This metadata is stored in a terraform branch off of the main git repo.

# Takes in GIT_USER, GIT_PASSWORD, and GIT_URL environment variables

# Validating environment variables
envVars=("GIT_USER" "GIT_PASSWORD" "GIT_URL")

for var in "${envVars[@]}"
do
	[ -z "${!var}" ] && echo "Skipping publishing terraform state to git repo: required environment variable ${var} not set... Please see the README for more information." && exit 0
done

# Create Git Repo URL
AUTH_GIT_URL=${GIT_URL:0:8}${GIT_USER}:${GIT_PASSWORD}@${GIT_URL:8}

git clone ${AUTH_GIT_URL} repo

cd repo

if [ $(git ls-remote --heads ${AUTH_GIT_URL} terraform | wc -l) == "0" ]; then
	git checkout -b terraform
	git push origin terraform
else
	git fetch origin terraform:terraform
	git checkout terraform
fi

cd -

exit 0
