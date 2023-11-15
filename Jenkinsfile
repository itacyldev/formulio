node(){
    configFileProvider([configFile(fileId: '0448eca8-c7dd-461c-8ec0-449f0706456e', variable: 'itConfig')]) {
        itCredentials = readJSON file: itConfig
    }       
}
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
                sh """
                    export CRTSYN_CRED_TESTSYN=${itCredentials['CRTSYN_CRED_TESTSYN']}
                    chmod +x gradlew
                    ./gradlew clean
                    ./gradlew test build
                """
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/build/test-results/testDebugUnitTest/*.xml'
                }
            }
        }

        stage('Report Jacoco') {
            steps {
                 sh './gradlew jacocoTestReport'
            }
            post {
                success {
                    jacoco(execPattern: '**/build/jacoco/*.exec')
                }
            }
        }
        //stage('Integration Test') {
        //    when {
        //        expression { BRANCH_NAME == 'develop' }
        //    }
        //    steps {
        //        script {
        //                sh '''#!/bin/bash
        //                export ANDROID_EMULATOR_HOME=/apps/android-sdk-linux/test
        //                export ANDROID_AVD_HOME=$ANDROID_EMULATOR_HOME/avd

        //                num_devices=$((`$ANDROID_HOME/platform-tools/adb devices|wc -l`-2))

        //                echo "num_devices: ${num_devices}"
        //               echo "-------------------------------------------------------------"
        //                echo "-------------------------------------------------------------"
        //                echo "Antes:"
        //                $ANDROID_HOME/platform-tools/adb devices
        //                echo "-------------------------------------------------------------"
        //                echo "-------------------------------------------------------------"

        //                if [ $num_devices -eq 0 ]; then
        //                    echo "Arrancando emulador..."
        //                    $ANDROID_HOME/emulator/emulator -avd nexus_6 -no-window -gpu guest -no-audio -read-only &
        //                    $ANDROID_HOME/platform-tools/adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done; input keyevent 82'
        //                fi

        //                echo "-------------------------------------------------------------"
        //                echo "-------------------------------------------------------------"
        //                echo "Después: "
        //                $ANDROID_HOME/platform-tools/adb devices
        //                echo "-------------------------------------------------------------"
        //                echo "-------------------------------------------------------------"

        //                # Copiar bd tests
        //                $ANDROID_HOME/platform-tools/adb push ${WORKSPACE}/forms/src/test/resources/ribera.sqlite /sdcard/test/ribera.sqlite

        //                # Copiar proyectos tests
        //                $ANDROID_HOME/platform-tools/adb push ${WORKSPACE}/forms/src/test/resources/config/project1 /sdcard/projects/project1

        //                ./gradlew :app:connectedAndroidTest --stacktrace --scan --no-parallel
        //            '''
        //        }
        //    }
        //    post {
        //        always {
        //            junit allowEmptyResults: true, testResults: '**/build/reports/androidTests/connected/*.xml'
        //        }
        //    }
        //}
//         stage('Report Jacoco') {
//             steps {
//                 sh './gradlew codeCoverageReport'
//             }
//         }
        stage('Assemble') {
            steps {
                script {
                    sh './gradlew :app:assembleRelease'
                }
            }
        }
        /*
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
        */
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