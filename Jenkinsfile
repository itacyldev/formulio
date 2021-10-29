// -- Directory where the Platform Tools is located
def PLATFORM_TOOL_DIRECTORY

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
                    PLATFORM_TOOL_DIRECTORY = "${env.ANDROID_HOME}"+"platform-tools/"
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
                    sh '''#!/bin/bash
                        export ANDROID_EMULATOR_HOME=/apps/android-sdk-linux/test
                        export ANDROID_AVD_HOME=$ANDROID_EMULATOR_HOME/avd

                        num_devices=$((`$ANDROID_HOME/platform-tools/adb devices|wc -l`-2))

                        echo "num_devices: ${num_devices}"
                        $ANDROID_HOME/platform-tools/adb devices

                        if [ $num_devices -eq 0 ]; then
                        	echo "Arrancando emulador..."
                        	$ANDROID_HOME/emulator/emulator -avd nexus_6 -no-window -gpu guest -no-audio -read-only &
                        	$ANDROID_HOME/platform-tools/adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done; input keyevent 82'
                        fi

                        # Copiar bd tests
                        $ANDROID_HOME/platform-tools/adb push ${WORKSPACE}/forms/src/test/resources/ribera.sqlite /sdcard/test/ribera.sqlite

                        # Copiar proyectos tests
                        $ANDROID_HOME/platform-tools/adb push ${WORKSPACE}/forms/src/test/resources/config/project1 /sdcard/projects/project1
                    '''
                }
            }
        }
    }
    post {
        failure {
            //emailext body: '''${SCRIPT, template="groovy-html.template"}''',
            //recipientProviders: [culprits()],
            //subject: "Build failed in jenkins: ${env.JOB_NAME} ${env.BUILD_NUMBER}",
            //mimeType: 'text/html'


            sh '''#!/bin/bash
                num_devices=$((`$ANDROID_HOME/platform-tools/adb devices|wc -l`-2))
                echo "num_devices: ${num_devices}"
                $ANDROID_HOME/platform-tools/adb devices
                if [ $num_devices -gt 0 ]; then
                    echo "Parando emulador..."
                    for device in `$ANDROID_HOME/platform-tools/adb devices`; do
                        if [ "$device" = "emulator"* ]; then
                            echo "adb -s $device $@"
                        fi
                    done
                fi
            '''
                                //cd ${PLATFORM_TOOL_DIRECTORY}
                                //adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill; done

                //for device in `adb devices | awk '{print $1}'`; do
                 //     if [ ! "$device" = "" ] && [ ! "$device" = "List" ]
                  //    then
                   //     echo " "
                    //    echo "adb -s $device $@"
                     //   echo "------------------------------------------------------"
                      //  adb -s $device $@ emu kill
                     // fi
                    //done
                    //echo "Parando emulador..."
                    //$ANDROID_HOME/platform-tools/adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill; done
                    //$ANDROID_HOME/platform-tools/adb kill-server
                    //$ANDROID_HOME/platform-tools/adb emu kill
            //'''
        }
    }
}