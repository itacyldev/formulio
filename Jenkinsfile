
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

                        # doing android emulator stuff manual
                        $ANDROID_HOME/platform-tools/adb start-server
                        $ANDROID_HOME/emulator/emulator -prop persist.sys.language=de -prop persist.sys.country=DE -avd $AVD_IMAGE -no-window -no-audio &
                        EMULATOR_PID=$!

                        # Wait for Android to finish booting
                        WAIT_CMD="$ANDROID_HOME/platform-tools/adb wait-for-device shell getprop init.svc.bootanim"
                        until $WAIT_CMD | grep -m 1 stopped; do
                          echo "Waiting..."
                          sleep 1
                        done

                        $ANDROID_HOME/platform-tools/adb devices

                        [ -d build ] || mkdir build
                        $ANDROID_HOME/platform-tools/adb shell logcat -v time > build/logcat.log &
                        LOGCAT_PID=$!
                        $ANDROID_HOME/platform-tools/adb shell wm dismiss-keyguard
                        $ANDROID_HOME/platform-tools/adb shell input keyevent 4

                        # run tests
                        ./gradlew --continue connectedAndroidTest || echo "failed"

                        # Stop the background processes
                        kill $LOGCAT_PID
                        kill $EMULATOR_PID

                        $ANDROID_HOME/platform-tools/adb devices
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
                        if [[ $device = emulator* ]]; then
                            echo "adb -s $device $@"
                            `$ANDROID_HOME/platform-tools/adb -s $device $@ emu kill`
                        fi
                    done
                fi
            '''
        }
    }
}