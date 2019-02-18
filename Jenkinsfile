@Library('vl-jenkins')_

import no.nav.jenkins.*

node('DOCKER') {
    Date date= new Date()
    GIT_COMMIT_HASH = sh (script: "git log -n 1 --pretty=format:'%h'", returnStdout: true)
    def revision = "5.0"
    def changelist = "_" + date.format("YYYYMMDDHHmmss") + "_" + GIT_COMMIT_HASH
    def tagName=revision + changelist
    
    maven = new maven()
    stage('Checkout Tags') { // checkout only tags.
        checkout([$class: 'GitSCM', branches: [[name: '*/tags/*']],
        doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [],
        userRemoteConfigs: [[refspec: '+refs/tags/*:refs/remotes/origin/tags/*',
        url: 'https://github.com/navikt/fp-formidling.git']]])
        tagName=sh(returnStdout: true, script: 'git describe --abbrev=0 --tags').toString().trim()
        echo "Tag to be deployed $tagName"
    }
   
    stage('Build') {
       configFileProvider(
           [configFile(fileId: 'navMavenSettingsUtenProxy', variable: 'MAVEN_SETTINGS')]) {
                
                buildEnvironment = new buildEnvironment()
                if(maven.javaVersion() != null) {
                    buildEnvironment.overrideJDK(maven.javaVersion())
                }
                
                sh "mvn -U -B -s $MAVEN_SETTINGS -Dfile.encoding=UTF-8 -DinstallAtEnd=true -DdeployAtEnd=true -Dsha1= -Dchangelist= -Drevision=$tagName clean deploy"
                
           }   
                
    }
}
