#!/bin/bash -eux

# Push the loxigen artifacts to a dedicated git repository,
# along with a nice commit message and a tag

ARTIFACT_REPO_URL="$1"
if [[ ! $ARTIFACT_REPO_URL ]]; then
    echo "Call syntax: $0 <artifact_repo_url>" >&2
    exit 1
fi

ARTIFACT_REPO_BRANCH=${2-master}
ARTIFACT_TARGET_BRANCH=${3-master}

MAKE=${MAKE-make}

ARTIFACT_REPO=$(mktemp -d --tmpdir "push-artifacts-repo.XXXXXXX")

git clone ${ARTIFACT_REPO_URL} ${ARTIFACT_REPO} -b ${ARTIFACT_REPO_BRANCH}

find ${ARTIFACT_REPO} -mindepth 1 -maxdepth 1 -type d \! -name '.*' -print0 | xargs -0 rm -r
${MAKE} LOXI_OUTPUT_DIR=${ARTIFACT_REPO} clean all

loxi_branch=$(git rev-parse --abbrev-ref HEAD)
loxi_head=$(git rev-parse HEAD)
last_loxi_log=$(git log --format=oneline -1)
git_log_file=$(mktemp --tmpdir "git-log-file.XXXXXXX")

last_loxi_revision=""

if [[ -e "${ARTIFACT_REPO}/loxi-revision" ]]; then
    last_loxi_revision=$(cat "${ARTIFACT_REPO}/loxi-revision" |  cut -d ' ' -f 1)
    if [[ $(git cat-file -t "$last_loxi_revision" 2>/dev/null) != "commit" ]]; then
        echo "Last loxi revision ${last_loxi_revision} specified in ${ARTIFACT_REPO_URL}/loxi-revision not found in loxigen repo"
        last_loxi_revision=""
    fi
fi

if [[ $last_loxi_revision ]]; then
    echo "Last loxi revision committed: $last_loxi_revision"
    git log $last_loxi_revision..${loxi_head} >>$git_log_file
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
       echo "Artifacts from ${loxi_github_url} (Branch ${loxi_branch})"
       echo
       echo "Loxigen Head commit floodlight/loxigen@${loxi_head}"
       cat $git_log_file
    ) | git commit --file=-

    git tag -a -f "loxi/${loxi_head}" -m "Tag Loxigen Revision ${loxi_head}"
    git push --tags -f
    if [[ $ARTIFACT_TARGET_BRANCH != $ARTIFACT_REPO_BRANCH ]]; then
        git push -f origin HEAD:${ARTIFACT_TARGET_BRANCH}
    else
        git push origin HEAD
    fi
)

rm -rf ${ARTIFACT_REPO}
