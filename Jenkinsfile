
pipeline {
    agent any

    environment {
        PROJECT_NAME = 'FRMDRD'
        GIT_URL = "https://servicios.itacyl.es/gitea/ITACyL/${PROJECT_NAME}.git"
    }

    stages {
        stage("Milestone check") {
            steps {
                script {
                    // Posibles builds en ejecución (se comprueban solo tres anteriores)
                    def buildNumber = env.BUILD_NUMBER as int
                    for (int i = buildNumber-3; i < buildNumber; i++){
                        echo ("Cancelando build: ${i}")
                        milestone(i)
                    }
                    milestone(buildNumber)
                }
            }
        }
        stage("Clone sources"){
            steps {
                git branch: "${BRANCH_NAME}", credentialsId: 'jenkins-gitea-user', url: "${GIT_URL}"
            }
        }
        stage("Build") {
                            steps {
                                script {
                                    sh """
                                        chmod +x gradlew
                                    """
                                }
                            }
                        }
        stage("Integration Test") {
            when {
                expression{BRANCH_NAME == 'develop'}
            }
            steps {
                script {
                    sh '''#!/bin/bash
                        export ANDROID_EMULATOR_HOME=/apps/android-sdk-linux/test
                        export ANDROID_AVD_HOME=$ANDROID_EMULATOR_HOME/avd

                        $ANDROID_HOME/platform-tools/adb start-server
                        $ANDROID_HOME/emulator/emulator -avd nexus_6 -no-window -gpu guest -no-audio -read-only &
                        $ANDROID_HOME/platform-tools/adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done; input keyevent 82'

                        # Copiar bd tests
                        $ANDROID_HOME/platform-tools/adb push ${WORKSPACE}/forms/src/test/resources/ribera.sqlite /sdcard/test/ribera.sqlite

                        # Copiar proyectos tests
                        $ANDROID_HOME/platform-tools/adb push ${WORKSPACE}/forms/src/test/resources/config/project1 /sdcard/projects/project1

                        ./gradlew clean connectedAndroidTest --stacktrace

                        $ANDROID_HOME/platform-tools/adb kill-server
                    '''
                }
            }
        }
        stage("Report Jacoco") {
            steps {
                script {
                    sh """
                        ./gradlew codeCoverageReport
                    """
                }
            }
        }
        stage("Sonarqube") {
            when {
                expression{BRANCH_NAME == 'develop'}
            }
            steps {
                script {
                    sh """
                        ./gradlew sonarqube
                    """
                }
            }
        }
    }
    
}