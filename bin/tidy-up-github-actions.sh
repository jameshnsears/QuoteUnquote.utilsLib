#!/bin/bash

source envvars.sh

if [ $# -ne 2 ]; then
  echo "Usage: $0 <BRANCH_WILDCARD> <NUMBER_OF_RUNS>"
  exit 1
fi

BRANCH=$1         # can be "specific" wildcard
NUMBER_OF_RUNS=$2

gh run list \
  --json headBranch,databaseId --jq '.[] | select(.headBranch | test("dependabot/")) | .databaseId' \
  -L 100 | \
xargs -IID gh api \
  "repos/$(gh repo view --json nameWithOwner -q .nameWithOwner)/actions/runs/ID" \
  -X DELETE
