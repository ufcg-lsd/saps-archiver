#!/bin/bash

readonly REPOSITORY=ufcgsaps/archiver
readonly USAGE="usage: docker.sh {build|push|publish} <TAG>"
readonly MY_PATH=$(cd "$(dirname "${0}")" || { echo "For some reason, the path is not accessible"; exit 1; }; pwd )
readonly WORKING_DIRECTORY="$(dirname "${MY_PATH}")"
readonly DOCKER_FILE_PATH="${MY_PATH}/Dockerfile"

readonly TEMP_STORAGE_DIRECTORY="/nfs"
readonly CONFIG_FILE_PATH="${WORKING_DIRECTORY}/config/archiver.conf"
readonly LOG4J_PROPERTIES_FILE_PATH="${WORKING_DIRECTORY}/config/log4j.properties"
readonly EXECUTION_TAGS_FILE_PATH="${WORKING_DIRECTORY}/resources/execution_script_tags.json"

readonly ARCHIVER_CONTAINER=saps-archiver
readonly ARCHIVER_NETWORK=saps-network

build() {
  local TAG="${1-latest}"
  docker build --tag "${REPOSITORY}":"${TAG}" \
            --file "${DOCKER_FILE_PATH}" "${WORKING_DIRECTORY}"
}

push() {
  local TAG="${1-latest}"
  docker push "${REPOSITORY}":"${TAG}"
}

run() {
  local TAG="${1}"
  docker run -dit \
    --name "${ARCHIVER_CONTAINER}" \
    --net="${SAPS_NETWORK}" --net-alias=archiver \
    -v "${CONFIG_FILE_PATH}":/etc/saps/archiver.conf \
    -v "${LOG4J_PROPERTIES_FILE_PATH}":/etc/saps/log4j.properties \
    -v "${TEMP_STORAGE_DIRECTORY}":/nfs \
    "${REPOSITORY}":"${TAG}"
}

main() {
  if [ "$#" -eq 0 ]; then
      echo "${USAGE}"
      exit 1
  fi
  case ${1} in
    build) shift
      build "$@"
      ;;
    push) shift
      push "$@"
      ;;
    publish) shift
      build "$@"
      push "$@"
      ;;
    run) shift
      run "$@"
      ;;
    *)
      echo "${USAGE}"
      exit 1
  esac
}

main "$@"