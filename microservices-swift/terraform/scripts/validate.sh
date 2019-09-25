#! /usr/bin/env bash

# required environment variables
envVars=("PUBLIC_KEY" "VI_INSTANCE_NAME" "TF_VAR_ibm_sl_api_key" "TF_VAR_ibm_sl_username" "TF_VAR_ibm_cloud_api_key" "PRIVATE_KEY")

echo "Validating environment variables..."

for var in "${envVars[@]}"
do
	[ -z "${!var}" ] && echo "Error: Required environment variable ${var} not set... Please see the README for more information." && exit 1
done

exit 0
