pipeline {
    agent any

    environment {
        PROJECT_NAME = 'FRMDRD'
        GIT_URL = "https://servicios.itacyl.es/gitea/ITACyL/${PROJECT_NAME}.git"
        ANDROID_EMULATOR_HOME= '/apps/android-sdk-linux/test'
        ANDROID_AVD_HOME='${ANDROID_EMULATOR_HOME}/avd'
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
        stage("Test") {
            steps {
                script {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean'
                    sh './gradlew test --stacktrace'
                }
            }
        }
        stage("Integration Test") {
            steps {
                script {

                    def num_devices=$((`$ANDROID_HOME/platform-tools/adb devices|wc -l`-2))

                    if [ $num_devices -eq 0 ]; then
                        sh 'echo "Arrancando emulador...."'

                    	sh '$ANDROID_HOME/emulator/emulator -avd nexus_6 -no-window -gpu guest -no-audio -read-only &'
                    	sh '$ANDROID_HOME/platform-tools/adb wait-for-device shell "while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done; input keyevent 82"'
                    fi

                    sh '$ANDROID_HOME/platform-tools/adb push ${WORKSPACE}/forms/src/test/resources/ribera.sqlite /sdcard/test/ribera.sqlite'

                    sh '$ANDROID_HOME/platform-tools/adb push ${WORKSPACE}/forms/src/test/resources/config/project1 /sdcard/projects/project1'
                }
            }
        }
        stage("Build") {
            steps {
                script {
                    sh './gradlew build'
                }
            }
        }
        stage("Report Jacoco") {
            steps {
                script {
                    sh './gradlew codeCoverageReport'
                }
            }
        }
        stage("Sonarqube") {
            when {
                // solo se lanza análisis de sonarQube en rama develop
                expression{BRANCH_NAME == 'develop'}
            }
            steps {
                script {
                    sh './gradlew sonarqube'
                }
            }
        }
    }   
}