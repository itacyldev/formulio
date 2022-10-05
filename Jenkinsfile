
pipeline {
    agent any

    environment {
        PROJECT_NAME = 'FRMDRD'
        //GIT_URL = "https://servicios.itacyl.es/gitea/ITACyL/${PROJECT_NAME}.git"
        GIT_URL = "git@itaul4622:ITACyL/${PROJECT_NAME}.git"
    }
    //options {
    //        skipDefaultCheckout(true)
    //    }
    stages {
        stage('Milestone check') {
            steps {
                script {
                    // Posibles builds en ejecución (se comprueban solo tres anteriores)
                    def buildNumber = env.BUILD_NUMBER as int
                    for (int i = buildNumber - 3; i < buildNumber; i++) {
                        echo("Cancelando build: ${i}")
                        milestone(i)
                    }
                    milestone(buildNumber)
                }
            }
        }
        stage('Clone sources') {
            steps {
                git branch: "${BRANCH_NAME}", credentialsId: 'jenkins-gitea-user', url: "${GIT_URL}"
            }
        }
        stage('Build & Unit test') {
            steps {
                script {
                    sh '''
                        chmod +x gradlew
                        ./gradlew clean
                        ./gradlew test build
                    '''
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/build/test-results/testDebugUnitTest/*.xml'
                }
            }
        }
        
        stage('Report Jacoco') {
            steps {
                sh './gradlew codeCoverageReport'
            }
        }
        stage('SonarQube analysis') {
            when {
                expression { BRANCH_NAME == 'develop' }
            }
            steps {
                withSonarQubeEnv('SonarQube-ITA') {
                    sh './gradlew sonarqube'
                }
            }
        }
        stage('Quality Gate') {
            when {
                expression { BRANCH_NAME == 'develop' }
            }
            steps {
                timeout(time: 20, unit: 'MINUTES') {
                    // Parameter indicates whether to set pipeline to UNSTABLE if Quality Gate fails
                    // true = set pipeline to UNSTABLE, false = don't
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
    post {
        failure {
            emailext body: '''${SCRIPT, template="groovy-html.template"}''',
            recipientProviders: [culprits()],
            subject: "Build failed in jenkins: ${env.JOB_NAME} ${env.BUILD_NUMBER}",
            mimeType: 'text/html'
        }
        always {
            sh '''#!/bin/bash
                num_devices=$((`$ANDROID_HOME/platform-tools/adb devices|wc -l`-2))
                echo "num_devices: ${num_devices}"
                $ANDROID_HOME/platform-tools/adb devices
                echo "Parando emulador..."
                for device in `$ANDROID_HOME/platform-tools/adb devices`; do
                    if [[ $device = emulator* ]]; then
                        echo "adb -s $device $@"
                        `$ANDROID_HOME/platform-tools/adb -s $device $@ emu kill`
                    fi
                done
            '''
        }
    }
}
