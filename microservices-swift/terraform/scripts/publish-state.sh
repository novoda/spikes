#! /usr/bin/env bash

# This script persists terraform state metadata for future fetching (see fetch-state.sh)
# This metadata is stored in a terraform branch off of the main git repo.

# git config
git config --global user.email "autobuild@not-an-email.com"
git config --global user.name "Automatic Build: ibmcloud-toolchain-${PIPELINE_TOOLCHAIN_ID}"
git config --global push.default simple

cd repo
git checkout terraform
git add .
git commit -m "Published terraform.tfstate from ibmcloud-toolchain-${PIPELINE_TOOLCHAIN_ID}"
git push --set-upstream origin terraform -f
cd -

exit 0
