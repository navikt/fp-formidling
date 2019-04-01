@Library('vl-jenkins') _

import no.nav.jenkins.*

def maven = new maven()
def fpgithub = new fpgithub()
def version
def GIT_COMMIT_HASH
def GIT_COMMIT_HASH_FULL
pipeline {
    agent any

    stages {

        stage('Checkout scm') { // checkout only tags.
            steps {
                script {
                    Date date = new Date()
                    dockerRegistryIapp = "repo.adeo.no:5443"

                    checkout scm
                    GIT_COMMIT_HASH = sh(script: "git log -n 1 --pretty=format:'%h'", returnStdout: true)
                    GIT_COMMIT_HASH_FULL = sh(script: "git log -n 1 --pretty=format:'%H'", returnStdout: true)
                    changelist = "_" + date.format("YYYYMMDDHHmmss") + "_" + GIT_COMMIT_HASH
                    mRevision = maven.revision()
                    version = mRevision + changelist

                    currentBuild.displayName = version

                    echo "Building $version"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    configFileProvider(
                            [configFile(fileId: 'navMavenSettings', variable: 'MAVEN_SETTINGS')]) {
                        artifactId = maven.artifactId()
                        buildEnvironment = new buildEnvironment()
                        if (maven.javaVersion() != null) {
                            buildEnvironment.overrideJDK(maven.javaVersion())
                        }

                        sh "mvn -U -B -s $MAVEN_SETTINGS -Dfile.encoding=UTF-8 -DinstallAtEnd=true -DdeployAtEnd=true -Dsha1= -Dchangelist= -Drevision=$version clean install"
                        sh "docker build --pull -t $dockerRegistryIapp/$artifactId:$version ."
                        withCredentials([[$class          : 'UsernamePasswordMultiBinding',
                                          credentialsId   : 'nexusUser',
                                          usernameVariable: 'NEXUS_USERNAME',
                                          passwordVariable: 'NEXUS_PASSWORD']]) {
                            sh "docker login -u ${env.NEXUS_USERNAME} -p ${env.NEXUS_PASSWORD} ${dockerRegistryIapp} && docker push ${dockerRegistryIapp}/${artifactId}:${version}"
                        }
                    }
                }
            }
        }

        stage('Tag master') {
            when {
                branch 'master'
            }
            steps {
                sh "git tag $version -m $version"
                sh "git push origin --tag"
            }
        }


    }

    post {
        success {
            script {
                fpgithub.updateBuildStatus("fp-formidling", "success", GIT_COMMIT_HASH_FULL)
            }
        }
        failure {
            script {
                fpgithub.updateBuildStatus("fp-fpformidling", "failure", GIT_COMMIT_HASH_FULL)
            }
        }
    }

}
