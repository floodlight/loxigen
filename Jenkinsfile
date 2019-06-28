// calculate maven patch version for Maven CI friendly revision
def getMavenPatchVersion() {
    switch(env.JOB_BASE_NAME) {
        case "master":
            // for master, just the build number (these are our "production" releases)
            return env.BUILD_NUMBER
        case ~/PR-\d+/:
            // for PRs, we use SNAPSHOTS
            return "${env.JOB_BASE_NAME}-SNAPSHOT"
        default:
            // for other branches "branchname".number
            return "${env.JOB_BASE_NAME}.${env.BUILD_NUMBER}"
    }
}

mavenPatchVersion=getMavenPatchVersion()
artifact_repo="git@github.com:floodlight/loxigen-artifacts.git"
artifact_base_branch=env.CHANGE_TARGET ?: env.JOB_BASE_NAME
artifact_target_branch= env.CHANGE_ID ? "${env.JOB_BASE_NAME}-b${env.BUILD_NUMBER}" : env.JOB_BASE_NAME

@Library("github") _

if(env.CHANGE_ID) {
    withCredentials([string(
                credentialsId: 'github-auth-token-bsn-abat',
                variable: 'GITHUB_AUTH_TOKEN') ]) {
        githubCheckOrgAuthz(env.CHANGE_URL, env.GITHUB_AUTH_TOKEN)
    }
}

pipeline {
    agent {
        dockerfile {
            dir 'docker'
            args """--entrypoint ''"""
        }
    }

    stages {
        stage("Prepare") {
            steps {
                echo "Maven Patch Version: ${mavenPatchVersion}"
                echo "artifact_base_branch: ${artifact_base_branch}"
                echo "artifact_target_branch: ${artifact_target_branch}"
            }
        }

        stage("Checkout infrastructure") {
            steps {
                sshagent(['ssh_jenkins_master']) {
                    dir('infrastructure') {
                        git(credentialsId: 'ssh_jenkins_master', url: 'git@github.com:bigswitch/infrastructure.git')
                    }
                }
            }
        }

        stage("Build") {
            steps {
                sh """
                    make clean all
                    """
            }
        }

        stage("Unit tests") {
            steps {
                sh """
                    make check check-all
                    """
            }
        }

        stage("Generate Artifacts") {
            steps {
                sshagent(['ssh_jenkins_master']) {
                    sh """
                        ./.build/push-artifacts.sh ${artifact_repo} ${artifact_base_branch} ${artifact_target_branch}
                    """
                }
           }
        }

        stage("Artifact Link") {
            when { branch 'PR-*' }
            steps {
                withCredentials([string(
                            credentialsId: 'github-auth-token-bsn-abat',
                            variable: 'GITHUB_AUTH_TOKEN') ]) {
                    sh """
                        ./infrastructure/build/githubtool/gh.py comment "Find artifact changes from this pull request at https://github.com/floodlight/loxigen-artifacts/tree/${artifact_target_branch}"
                        """
                }
            }
        }

        stage("Deploy OpenflowJ") {
            steps {
                withCredentials([file(credentialsId: 'maven-code-signing-key', variable: 'MAVEN_SIGNING_KEY_FILE')]) {
                    sh """ gpg --fast-import <${MAVEN_SIGNING_KEY_FILE} """
                }
                configFileProvider( [configFile(fileId: 'maven-settings-xml', variable: 'MAVEN_SETTINGS')]) {
                    sh """
                        make clean java
                        cd loxi_output/openflowj
                        JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 mvn --batch-mode -s $MAVEN_SETTINGS -Prelease -Psign -Drevision=${mavenPatchVersion} package deploy
                    """
                }
                sh """
                    gpg --batch --yes --delete-secret-key 'EF31 1B74 EE63 3982 2335  F1B2 68B8 8023 E9F9 33D3'
                """
            }
        }
    }
}
