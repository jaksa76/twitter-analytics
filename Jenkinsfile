pipeline {
    agent any
    environment {
        BUILD_NO = "${BUILD_NUMBER}"
    }
    stages {
        stage('Build') {
            steps {
                sh 'cp /usr/share/service-account.json ./service-account.json'
                sh 'mvn package'
                sh 'docker-compose build'
                sh 'docker tag web-app:${BUILD_NUMBER} eu.gcr.io/genuine-axe-182507/web-app:${BUILD_NUMBER}'
                sh 'docker tag master:${BUILD_NUMBER} eu.gcr.io/genuine-axe-182507/master:${BUILD_NUMBER}'
                sh 'docker tag worker:${BUILD_NUMBER} eu.gcr.io/genuine-axe-182507/worker:${BUILD_NUMBER}'
                sh '/usr/share/google-cloud-sdk/bin/gcloud docker -- push eu.gcr.io/genuine-axe-182507/web-app:${BUILD_NUMBER}'
                sh '/usr/share/google-cloud-sdk/bin/gcloud docker -- push eu.gcr.io/genuine-axe-182507/master:${BUILD_NUMBER}'
                sh '/usr/share/google-cloud-sdk/bin/gcloud docker -- push eu.gcr.io/genuine-axe-182507/worker:${BUILD_NUMBER}'
            }
        }
    }
}