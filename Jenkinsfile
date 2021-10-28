// -- Directory where the Platform Tools is located
def PLATFORM_TOOL_DIRECTORY
// -- Directory where the Android Emulator is located
def EMULATOR_DIRECTORY
// -- Devices
def NUM_DEVICES

def PWD




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

                    sh '''#!/bin/bash
                        export ANDROID_EMULATOR_HOME=/apps/android-sdk-linux/test
                        export ANDROID_AVD_HOME=$ANDROID_EMULATOR_HOME/avd

                        num_devices=$((`$ANDROID_HOME/platform-tools/adb devices|wc -l`-2))

                        $ANDROID_HOME/platform-tools/adb -s emulator-5556 emu kill
                        $ANDROID_HOME/platform-tools/adb -s emulator-5558 emu kill
                        $ANDROID_HOME/platform-tools/adb -s emulator-5560 emu kill
                        $ANDROID_HOME/platform-tools/adb -s emulator-5562 emu kill
                        $ANDROID_HOME/platform-tools/adb -s emulator-5564 emu kill
                        $ANDROID_HOME/platform-tools/adb -s emulator-5566 emu kill
                        $ANDROID_HOME/platform-tools/adb -s emulator-5568 emu kill
                        $ANDROID_HOME/platform-tools/adb -s emulator-5570 emu kill

                        $ANDROID_HOME/platform-tools/adb devices|wc -l

                        if [ $num_devices -eq 0 ]; then
                        	echo "Arrancando emulador...."
                        	$ANDROID_HOME/emulator/emulator -avd nexus_6 -no-window -gpu guest -no-audio -read-only &
                        	$ANDROID_HOME/platform-tools/adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done; input keyevent 82'
                        fi

                        # Copiar bd tests
                        $ANDROID_HOME/platform-tools/adb push ${WORKSPACE}/forms/src/test/resources/ribera.sqlite /sdcard/test/ribera.sqlite

                        # Copiar proyectos tests
                        $ANDROID_HOME/platform-tools/adb push ${WORKSPACE}/forms/src/test/resources/config/project1 /sdcard/projects/project1
                    '''


                    //dir("${PLATFORM_TOOL_DIRECTORY}") {
                     //   NUM_DEVICES = sh(script: './adb devices|wc -l', returnStdout: true)
                     //   echo "NUM_DEVICES: ${NUM_DEVICES}"
                    //}

                    //adb devices | grep "emulator-" | while read -r emulator device; do
//                      adb -s $emulator emu kill
  //                  done

                    //sh """
                     //   cd ${PLATFORM_TOOL_DIRECTORY}
                      //  ./adb devices | wc - l
                       // ./adb -s emulator-5554 push ${env.WORKSPACE}/forms/src/test/resources/ribera.sqlite /sdcard/test/ribera.sqlite
                        //./adb -s emulator-5554 push ${env.WORKSPACE}/forms/src/test/resources/config/project1 /sdcard/projects/project1
                    //"""
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