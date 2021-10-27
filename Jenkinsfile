// -- Directory where the Platform Tools is located
def PLATFORM_TOOL_DIRECTORY
// -- Directory where the Android Emulator is located
def EMULATOR_DIRECTORY
// -- Devices
def NUM_DEVICES


pipeline {
    agent any

    environment {
        PROJECT_NAME = 'FRMDRD'
        GIT_URL = "https://servicios.itacyl.es/gitea/ITACyL/${PROJECT_NAME}.git"
    }

    stages {
        stage("Initial Configuration") {
            steps {
                script {
                    ANDROID_EMULATOR_HOME= '/apps/android-sdk-linux/test'
                    ANDROID_AVD_HOME="${ANDROID_EMULATOR_HOME}/avd"
                    PLATFORM_TOOL_DIRECTORY = "${env.ANDROID_HOME}"+"platform-tools/"
                    EMULATOR_DIRECTORY = "${env.ANDROID_HOME}"+"emulator/"
                    NUM_DEVICES = sh(script: 'cd ${PLATFORM_TOOL_DIRECTORY} && ./adb devices|wc -l', returnStdout: true)
                }
            }
        }
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
        stage("Build") {
            steps {
                script {
                    sh './gradlew build'
                }
            }
        }

        stage("Integration Test") {
            steps {
                script {
                    echo "ANDROID_EMULATOR_HOME: ${ANDROID_EMULATOR_HOME}"
                    echo "ANDROID_AVD_HOME: ${ANDROID_AVD_HOME}"
                    echo "PLATFORM_TOOL_DIRECTORY: ${PLATFORM_TOOL_DIRECTORY}"
                    echo "EMULATOR_DIRECTORY: ${EMULATOR_DIRECTORY}"
                    sh """
                        echo "NUM_DEVICES = ${NUM_DEVICES}"
                    """
                    if (${NUM_DEVICES} <= 2){
                        echo "Arrancando emulador...."
                        cd ${EMULATOR_DIRECTORY}
                        sh './emulator -avd nexus_6 -no-window -gpu guest -no-audio -read-only &'
                        cd ${PLATFORM_TOOL_DIRECTORY}
                        sh './adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done; input keyevent 82'
                    }

                    cd ${PLATFORM_TOOL_DIRECTORY}
                    sh './adb push ${WORKSPACE}/forms/src/test/resources/ribera.sqlite /sdcard/test/ribera.sqlite'
                    sh './adb push ${WORKSPACE}/forms/src/test/resources/config/project1 /sdcard/projects/project1'
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