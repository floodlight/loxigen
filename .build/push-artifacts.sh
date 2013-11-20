#!/bin/bash

# Push the loxigen artifacts to a dedicated git repository,
# along with a nice commit message and a tag

ARTIFACT_REPO_URL="$1"
if [[ ! $ARTIFACT_REPO_URL ]]; then
    echo "Call syntax: $0 <artifact_repo_url>" >&2
    exit 1
fi

set -e
ARTIFACT_REPO=$(mktemp -d)

git clone ${ARTIFACT_REPO_URL} ${ARTIFACT_REPO}
make LOXI_OUTPUT_DIR=${ARTIFACT_REPO} clean all

last_msg=$(cd $ARTIFACT_REPO && git log -1)
last_loxi_revision=$(echo "$last_msg" | perl -n -e 'm{Loxigen HEAD commit floodlight/loxigen@([a-f0-9]+)} && print $1')

loxi_head=$(git rev-parse HEAD)
last_loxi_log=$(git log --format=oneline -1)
git_log_file=$(mktemp)
if [[ $last_loxi_revision ]]; then
    echo "Last loxi revision committed: $last_loxi_revision"
    git log $last_loxi_revision..HEAD >>$git_log_file
    loxi_github_url="https://github.com/floodlight/loxigen/compare/${last_loxi_revision}...${loxi_head}"
else
    echo "No Previous loxi revision info found"
    git log -1 HEAD >>$git_log_file
    loxi_github_url="https://github.com/floodlight/loxigen/commit/${loxi_head}"
fi


(
    set -xe
    cd $ARTIFACT_REPO
    echo $last_loxi_log >loxi-revision
    git add -A

    (
       echo "Artifacts from ${loxi_github_url}"
       echo
       echo "Loxigen HEAD commit floodlight/loxigen@${loxi_head}"
       echo
       cat $git_log_file
    ) | git commit --file=-

    git tag -a -f "loxi/${loxi_head}" -m "Tag Loxigen Revision ${loxi_head}"
    git push
)

rm -rf ${ARTIFACT_REPO}
