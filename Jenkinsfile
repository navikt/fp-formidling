@Library('vl-jenkins')_

import no.nav.jenkins.*

node('DOCKER') {
    Date date= new Date()
    def tagName
    
    maven = new maven()
    stage('Checkout Tags') { // checkout only tags.
        checkout scm
        GIT_COMMIT_HASH = sh (script: "git log -n 1 --pretty=format:'%h'", returnStdout: true)
        changelist = "_" + date.format("YYYYMMDDHHmmss") + "_" + GIT_COMMIT_HASH
        mRevision = maven.revision()
        tagName = mRevision + changelist
        echo "Tag to be deployed $tagName"
    }
   
    stage('Build') {
       configFileProvider(
           [configFile(fileId: 'navMavenSettings', variable: 'MAVEN_SETTINGS')]) {
                
                buildEnvironment = new buildEnvironment()
                if(maven.javaVersion() != null) {
                    buildEnvironment.overrideJDK(maven.javaVersion())
                }
                
                sh "mvn -U -B -s $MAVEN_SETTINGS -Dfile.encoding=UTF-8 -DinstallAtEnd=true -DdeployAtEnd=true -Dsha1= -Dchangelist= -Drevision=$version clean install"
                sh "docker build --pull -t $dockerRegistryIapp/$artifactId:$version ."
                withCredentials([[$class    : 'UsernamePasswordMultiBinding',
                        credentialsId       : 'nexusUser',
                        usernameVariable    : 'NEXUS_USERNAME',
                        passwordVariable    : 'NEXUS_PASSWORD']]) {
                            sh "docker login -u ${env.NEXUS_USERNAME} -p ${env.NEXUS_PASSWORD} ${dockerRegistryIapp} && docker push ${dockerRegistryIapp}/${artifactId}:${version}"
                        }
           }   
                
    }
}
