pipeline {
    agent any

    stages {
        stage("Git") {
            steps {
                git branch: 'develop', credentialsId: 'jenkins-gitea-user', url: 'https://servicios.itacyl.es/gitea/ITACyL/FRMDRD.git'
            }
        }
        stage("Test") {
            steps {
                script {
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
            steps {
                script {
                    sh './gradlew sonarqube'
                }
            }
        }
    }   
}