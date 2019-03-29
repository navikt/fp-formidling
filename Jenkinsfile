@Library('vl-jenkins') _

import no.nav.jenkins.*

def maven = new maven()
def version

pipeline {
    agent {
        node {
            label 'DOCKER2'
        }
    }

    options {
        timestamps()
    }

    environment {
        DOCKERREGISTRY = "docker.adeo.no:5000"
        ARTIFACTID = readMavenPom().getArtifactId()
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    checkout scm
                    gitCommitHash = sh(script: "git log -n 1 --pretty=format:'%h'", returnStdout: true)

                    mRevision = maven.revision()
                    Date date = new Date()
                    changelist = "_" + date.format("YYYYMMddHHmmss") + "_" + gitCommitHash

                    version = mRevision + changelist
                    echo "Tag to be deployed $version"
                }
            }

        }
        stage('Maven version') {
            steps {
                sh "mvn --version"
            }
        }

        stage('Build') {
            steps {
                script {

                    withMaven(globalMavenSettingsConfig: 'navMavenSettings', jdk: "${maven.javaVersion()}") {
                        try {
                            sh "mvn -B -Dfile.encoding=UTF-8 -DinstallAtEnd=true -DdeployAtEnd=true -Dsha1= -Dchangelist= -Drevision=$version clean install"
                            sh "docker build --pull -t $DOCKERREGISTRY/$ARTIFACTID:$version ."
                            withCredentials([[$class          : 'UsernamePasswordMultiBinding',
                                              credentialsId   : 'nexusUser',
                                              usernameVariable: 'NEXUS_USERNAME',
                                              passwordVariable: 'NEXUS_PASSWORD']]) {
                                sh "docker login -u ${env.NEXUS_USERNAME} -p ${env.NEXUS_PASSWORD} ${DOCKERREGISTRY} && docker push ${DOCKERREGISTRY}/${ARTIFACTID}:${version}"
                            }
                        } catch (Exception err) {
                            echo "Maven clean install feilet!"
                            currentBuild.result = 'FAILURE'
                        }
                    }
                }
            }
        }
    }
}
