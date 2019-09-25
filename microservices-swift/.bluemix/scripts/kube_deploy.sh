#!/bin/bash
#set -x

#View build properties
cat build.properties

echo "Check cluster availability"
IP_ADDR=$(ibmcloud cs workers ${PIPELINE_KUBERNETES_CLUSTER_NAME} | grep normal | head -n 1 | awk '{ print $2 }')
if [ -z $IP_ADDR ]; then
    echo "$PIPELINE_KUBERNETES_CLUSTER_NAME not created or workers not ready"
    exit 1
fi

echo "Configuring cluster namespace"
if kubectl get namespace ${CLUSTER_NAMESPACE}; then
  echo -e "Namespace ${CLUSTER_NAMESPACE} found."
else
  kubectl create namespace ${CLUSTER_NAMESPACE}
  echo -e "Namespace ${CLUSTER_NAMESPACE} created."
fi

echo "Configuring cluster role binding"
if kubectl get clusterrolebinding kube-system:default; then
  echo -e "Cluster role binding found."
else
  kubectl create clusterrolebinding kube-system:default --clusterrole=cluster-admin --serviceaccount=kube-system:default
  echo -e "Cluster role binding created."
fi

echo "Configuring Tiller (Helm's server component)"
helm init --upgrade
kubectl rollout status -w deployment/tiller-deploy --namespace=kube-system
while [ "$(helm version | grep "Tiller")" != "" ]; do
  echo "Waiting for server..."
  sleep 10
done
helm version

echo "CHART_NAME: $CHART_NAME"

echo "DEFINE RELEASE by prefixing image (app) name with namespace if not 'default' as Helm needs unique release names across namespaces"
if [[ "${CLUSTER_NAMESPACE}" != "default" ]]; then
  RELEASE_NAME="${CLUSTER_NAMESPACE}-${IMAGE_NAME}"
else
  RELEASE_NAME=${IMAGE_NAME}
fi
echo "RELEASE_NAME: $RELEASE_NAME"

echo "CHECKING CHART (lint)"
helm lint chart/${CHART_NAME}

IMAGE_REPOSITORY=${REGISTRY_URL}/${REGISTRY_NAMESPACE}/${IMAGE_NAME}

# Using 'upgrade --install" for rolling updates. Note that subsequent updates will occur in the same namespace the release is currently deployed in, ignoring the explicit--namespace argument".
echo -e "Dry run into: ${PIPELINE_KUBERNETES_CLUSTER_NAME}/${CLUSTER_NAMESPACE}."
helm upgrade --install --debug --dry-run ${RELEASE_NAME} ./chart/${CHART_NAME} --namespace ${CLUSTER_NAMESPACE} --set image.repository=${IMAGE_REPOSITORY},image.tag=${BUILD_NUMBER}

echo -e "Deploying into: ${PIPELINE_KUBERNETES_CLUSTER_NAME}/${CLUSTER_NAMESPACE}."
helm upgrade  --install ${RELEASE_NAME} ./chart/${CHART_NAME} --namespace ${CLUSTER_NAMESPACE} --set image.repository=${IMAGE_REPOSITORY},image.tag=${BUILD_NUMBER}

echo -e "CHECKING deployment status of release ${RELEASE_NAME} with image tag: ${BUILD_NUMBER}"
echo ""
for ITERATION in {1..30}
do
  DATA=$( kubectl get pods --namespace ${CLUSTER_NAMESPACE} -a -l release=${RELEASE_NAME} -o json )
  NOT_READY=$( echo $DATA | jq '.items[].status.containerStatuses[] | select(.image=="'"${IMAGE_REPOSITORY}:${BUILD_NUMBER}"'") | select(.ready==false) ' )
  if [[ -z "$NOT_READY" ]]; then
    echo -e "All pods are ready:"
    echo $DATA | jq '.items[].status.containerStatuses[] | select(.image=="'"${IMAGE_REPOSITORY}:${BUILD_NUMBER}"'") | select(.ready==true) '
    break # deployment succeeded
  fi
  REASON=$(echo $DATA | jq '.items[].status.containerStatuses[] | select(.image=="'"${IMAGE_REPOSITORY}:${BUILD_NUMBER}"'") | .state.waiting.reason')
  echo -e "${ITERATION} : Deployment still pending..."
  echo -e "NOT_READY:${NOT_READY}"
  echo -e "REASON: ${REASON}"
  if [[ ${REASON} == *ErrImagePull* ]] || [[ ${REASON} == *ImagePullBackOff* ]]; then
    echo "Detected ErrImagePull or ImagePullBackOff failure. "
    echo "Please check proper authenticating to from cluster to image registry (e.g. image pull secret)"
    break; # no need to wait longer, error is fatal
  elif [[ ${REASON} == *CrashLoopBackOff* ]]; then
    echo "Detected CrashLoopBackOff failure. "
    echo "Application is unable to start, check the application startup logs"
    break; # no need to wait longer, error is fatal
  fi
  sleep 5
done

if [[ ! -z "$NOT_READY" ]]; then
  echo ""
  echo "=========================================================="
  echo "DEPLOYMENT FAILED"
  echo "Deployed Services:"
  kubectl describe services ${RELEASE_NAME}-${CHART_NAME} --namespace ${CLUSTER_NAMESPACE}
  echo ""
  echo "Deployed Pods:"
  kubectl describe pods --selector app=${CHART_NAME} --namespace ${CLUSTER_NAMESPACE}
  echo ""
  echo "Application Logs"
  kubectl logs --selector app=${CHART_NAME} --namespace ${CLUSTER_NAMESPACE}
  echo "=========================================================="
  PREVIOUS_RELEASE=$( helm history ${RELEASE_NAME} | grep SUPERSEDED | sort -r -n | awk '{print $1}' | head -n 1 )
  echo -e "Could rollback to previous release: ${PREVIOUS_RELEASE} using command:"
  echo -e "helm rollback ${RELEASE_NAME} ${PREVIOUS_RELEASE}"
  # helm rollback ${RELEASE_NAME} ${PREVIOUS_RELEASE}
  # echo -e "History for release:${RELEASE_NAME}"
  # helm history ${RELEASE_NAME}
  # echo "Deployed Services:"
  # kubectl describe services ${RELEASE_NAME}-${CHART_NAME} --namespace ${CLUSTER_NAMESPACE}
  # echo ""
  # echo "Deployed Pods:"
  # kubectl describe pods --selector app=${CHART_NAME} --namespace ${CLUSTER_NAMESPACE}
  exit 1
fi

echo ""
echo "=========================================================="
echo "DEPLOYMENT SUCCEEDED"
echo ""
echo -e "Status for release:${RELEASE_NAME}"
helm status ${RELEASE_NAME}

echo ""
echo -e "History for release:${RELEASE_NAME}"
helm history ${RELEASE_NAME}

# echo ""
# echo "Deployed Services:"
# kubectl describe services ${RELEASE_NAME}-${CHART_NAME} --namespace ${CLUSTER_NAMESPACE}
# echo ""
# echo "Deployed Pods:"
# kubectl describe pods --selector app=${CHART_NAME} --namespace ${CLUSTER_NAMESPACE}

echo "=========================================================="
IP_ADDR=$(ibmcloud cs workers ${PIPELINE_KUBERNETES_CLUSTER_NAME} | grep normal | head -n 1 | awk '{ print $2 }')
PORT=$(kubectl get services --namespace ${CLUSTER_NAMESPACE} | grep ${RELEASE_NAME} | sed 's/[^:]*:\([0-9]*\).*/\1/g')
echo -e "View the application health at: http://${IP_ADDR}:${PORT}/health"
