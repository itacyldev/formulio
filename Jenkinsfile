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
        stage("Integration Test") {
            steps {
                script {
                    echo "ANDROID_EMULATOR_HOME: ${ANDROID_EMULATOR_HOME}"
                    echo "ANDROID_AVD_HOME: ${ANDROID_AVD_HOME}"
                    echo "PLATFORM_TOOL_DIRECTORY: ${PLATFORM_TOOL_DIRECTORY}"
                    echo "EMULATOR_DIRECTORY: ${EMULATOR_DIRECTORY}"
                    echo "WORKSPACE: ${env.WORKSPACE}"

                    sh """
                        cd ${PLATFORM_TOOL_DIRECTORY}
                    """
                    sh(returnStdout: true, script: 'export ANDROID_EMULATOR_HOME=/apps/android-sdk-linux/test')
                    sh(returnStdout: true, script: 'export ANDROID_AVD_HOME=$ANDROID_EMULATOR_HOME/avd')

                    NUM_DEVICES = sh(returnStdout: true, script: './adb devices')
                    echo "NUM_DEVICES: ${NUM_DEVICES}"
                }
            }
        }
        stage("Test") {
            steps {
                script {
                    sh """
                        chmod +x gradlew
                        ./gradlew clean
                        ./gradlew test --stacktrace
                    """
                }
            }
        }
        stage("Build") {
            steps {
                script {
                    sh """
                        ./gradlew build
                    """
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