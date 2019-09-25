#!/bin/bash
# set -x

echo "Build environment variables:"
echo "REGISTRY_URL=${REGISTRY_URL}"
echo "REGISTRY_NAMESPACE=${REGISTRY_NAMESPACE}"
echo "IMAGE_NAME=${IMAGE_NAME}"
echo "BUILD_NUMBER=${BUILD_NUMBER}"
echo "ARCHIVE_DIR=${ARCHIVE_DIR}"

# also run 'env' command to find all available env variables
# or learn more about the available environment variables at:
# https://cloud.ibm.com/docs/services/ContinuousDelivery/pipeline_deploy_var.html#deliverypipeline_environment

# To review or change build options use:
# ibmcloud cr build --help

echo "Checking registry namespace: ${REGISTRY_NAMESPACE}"
NS=$( ibmcloud cr namespaces | grep ${REGISTRY_NAMESPACE} ||: )
if [ -z "${NS}" ]; then
    echo -e "Registry namespace ${REGISTRY_NAMESPACE} not found, creating it."
    ibmcloud cr namespace-add ${REGISTRY_NAMESPACE}
    echo -e "Registry namespace ${REGISTRY_NAMESPACE} created."
else
    echo -e "Registry namespace ${REGISTRY_NAMESPACE} found."
fi

echo -e "Existing images in registry"
ibmcloud cr images

echo "=========================================================="
echo -e "BUILDING CONTAINER IMAGE: ${IMAGE_NAME}:${BUILD_NUMBER}"
set -x
ibmcloud cr build -t ${REGISTRY_URL}/${REGISTRY_NAMESPACE}/${IMAGE_NAME}:${BUILD_NUMBER} .
set +x
ibmcloud cr image-inspect ${REGISTRY_URL}/${REGISTRY_NAMESPACE}/${IMAGE_NAME}:${BUILD_NUMBER}

export PIPELINE_IMAGE_URL="$REGISTRY_URL/$REGISTRY_NAMESPACE/$IMAGE_NAME:$BUILD_NUMBER"

ibmcloud cr images

echo "=========================================================="
echo "COPYING ARTIFACTS needed for deployment and testing (in particular build.properties)"

echo "Checking archive dir presence"
mkdir -p $ARCHIVE_DIR

# Persist env variables into a properties file (build.properties) so that all pipeline stages consuming this
# build as input and configured with an environment properties file valued 'build.properties'
# will be able to reuse the env variables in their job shell scripts.

# CHART information from build.properties is used in Helm Chart deployment to set the release name
CHART_NAME=$(find chart/. -maxdepth 2 -type d -name '[^.]?*' -printf %f -quit)
echo "CHART_NAME=${CHART_NAME}" >> $ARCHIVE_DIR/build.properties
# IMAGE information from build.properties is used in Helm Chart deployment to set the release name
echo "IMAGE_NAME=${IMAGE_NAME}" >> $ARCHIVE_DIR/build.properties
echo "BUILD_NUMBER=${BUILD_NUMBER}" >> $ARCHIVE_DIR/build.properties
# REGISTRY information from build.properties is used in Helm Chart deployment to generate cluster secret
echo "REGISTRY_URL=${REGISTRY_URL}" >> $ARCHIVE_DIR/build.properties
echo "REGISTRY_NAMESPACE=${REGISTRY_NAMESPACE}" >> $ARCHIVE_DIR/build.properties
echo "File 'build.properties' created for passing env variables to subsequent pipeline jobs:"
cat $ARCHIVE_DIR/build.properties

echo "Copy pipeline scripts along with the build"
# Copy scripts (incl. deploy scripts)
if [ -d ./scripts/ ]; then
  if [ ! -d $ARCHIVE_DIR/scripts/ ]; then # no need to copy if working in ./ already
    cp -r ./scripts/ $ARCHIVE_DIR/
  fi
fi

echo "Copy Helm chart along with the build"
if [ ! -d $ARCHIVE_DIR/chart/ ]; then # no need to copy if working in ./ already
  cp -r ./chart/ $ARCHIVE_DIR/
fi
