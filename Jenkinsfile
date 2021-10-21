pipeline {
    agent any

    stages {
        stage("Git") {
            steps {
                git branch: 'develop', credentialsId: 'jenkins-gitea-user', url: 'https://servicios.itacyl.es/gitea/ITACyL/FRMDRD.git'
            }
        }
        stage("Test") {
            script{
                sh 'gradlew clean'
                sh 'gradlew test --stacktrace'
            }
        }
        stage("Build") {
            script{
                sh 'gradlew build'
            }
        }
        stage("Report Jacoco") {
            script{
                sh 'gradlew codeCoverageReport'
            }
        }
        stage("Sonarqube") {
            script{
                sh 'gradlew sonarqube'
            }
        }
    }   
}