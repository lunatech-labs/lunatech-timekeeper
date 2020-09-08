#!/bin/sh
# Configure this script to build the REACT app as a Clever-cloud CC_PRE_BUILD_HOOK
# using the clever-cli
# user@host > clever env set CC_PRE_BUILD_HOOK ./frontend/clevercloud.sh
# See https://www.clever-cloud.com/blog/features/2019/10/21/ghost-hosting-clever-cloud/
# I recommend also to use a dedicated 3XL instance to build the app
export REACT_APP_QUARKUS_BACKEND=https://acceptance.api.timekeeper.lunatech.fr
cd frontend || return
yarn install
yarn --max-old-space-size=4096 build
