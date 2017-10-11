pipeline {
    agent any

    stages {
        stage('Build') {
            environment {
                BUILD_NO = ${BUILD_NUMBER}
            }
            steps {
                sh 'cp /usr/share/service-account.json ./service-account.json'
                sh 'mvn package'
                sh 'docker-compose build'
                sh 'docker tag web-app:twitter-web-app eu.gcr.io/genuine-axe-182507/web-app:twitter-web-app'
                sh 'docker tag master:master-service eu.gcr.io/genuine-axe-182507/master:master-service'
                sh 'docker tag worker:twitter-worker eu.gcr.io/genuine-axe-182507/worker:twitter-worker'
                sh '/usr/share/google-cloud-sdk/bin/gcloud docker -- push eu.gcr.io/genuine-axe-182507/web-app:twitter-web-app'
                sh '/usr/share/google-cloud-sdk/bin/gcloud docker -- push eu.gcr.io/genuine-axe-182507/master:master-service'
                sh '/usr/share/google-cloud-sdk/bin/gcloud docker -- push eu.gcr.io/genuine-axe-182507/worker:twitter-worker'
            }
        }
    }
}