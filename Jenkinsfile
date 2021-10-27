pipeline {
    agent any

    environment {
        PROJECT_NAME = 'FRMDRD'
        GIT_URL = "https://servicios.itacyl.es/gitea/ITACyL/${PROJECT_NAME}.git"

        ANDROID_EMULATOR_HOME= '/apps/android-sdk-linux/test'
        ANDROID_AVD_HOME="${ANDROID_EMULATOR_HOME}/avd"

        ADB="${env.ANDROID_HOME}platform-tools"
        EMULATOR="${env.ANDROID_HOME}emulator"
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
                    echo "ANDROID_EMULATOR_HOME: ${ANDROID_EMULATOR_HOME}"
                    echo "ANDROID_AVD_HOME: ${ANDROID_AVD_HOME}"
                    echo "ADB: ${ADB}"
                    echo "EMULATOR: ${EMULATOR}"

                    dir ('${ADB}') {
                        def num_devices=sh "adb devices|wc -l" -2
                    }

                    echo "num_devices: ${num_devices}"

                    sh 'chmod +x gradlew'
                    sh './gradlew clean'
                    sh './gradlew test --stacktrace'
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