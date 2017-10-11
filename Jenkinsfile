pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mvn package'
                sh 'docker-compose build'
                sh 'docker tag web-app:twitter-web-app eu.gcr.io/genuine-axe-182507/web-app:twitter-web-app'
                sh 'docker tag master:master-service eu.gcr.io/genuine-axe-182507/master:master-service'
                sh 'docker tag worker:twitter-worker eu.gcr.io/genuine-axe-182507/worker:twitter-worker'
            }
        }
    }
}