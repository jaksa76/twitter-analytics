pipeline {
    agent any
    environment {
        BUILD_NO = "${BUILD_NUMBER}"
    }
    stages {
        stage('Build') {
            steps {
                sh 'cp /usr/share/service-account.json ./service-account.json'
                sh 'mvn package'            }
        }
        stage('Docker Compose') {
            steps {
                sh 'docker-compose build'
                sh 'docker tag web-app:${BUILD_NUMBER} eu.gcr.io/genuine-axe-182507/web-app:${BUILD_NUMBER}'
                sh 'docker tag master:${BUILD_NUMBER} eu.gcr.io/genuine-axe-182507/master:${BUILD_NUMBER}'
                sh 'docker tag worker:${BUILD_NUMBER} eu.gcr.io/genuine-axe-182507/worker:${BUILD_NUMBER}'            }
        }
        stage('Docker Push') {
            steps {
                sh '/usr/share/google-cloud-sdk/bin/gcloud docker -- push eu.gcr.io/genuine-axe-182507/web-app:${BUILD_NUMBER}'
                sh '/usr/share/google-cloud-sdk/bin/gcloud docker -- push eu.gcr.io/genuine-axe-182507/master:${BUILD_NUMBER}'
                sh '/usr/share/google-cloud-sdk/bin/gcloud docker -- push eu.gcr.io/genuine-axe-182507/worker:${BUILD_NUMBER}'
            }
        }
        stage('Docker Deploy') {
            steps {
                KUBE_OUTPUT = sh (
                    script: '/usr/share/google-cloud-sdk/bin/kubectl get deployments master-deploymentsasdf',
                    returnStdout: true
                ).trim()
                echo "Kubeoutput : ${KUBE_OUTPUT}"
                if(KUBE_OUTPUT.isEmpty()) {
                    echo "Creating missing master-deployment..."
                }

                KUBE_OUTPUT2 = sh (
                    script: '/usr/share/google-cloud-sdk/bin/kubectl get deployments master-deployments',
                    returnStdout: true
                ).trim()
                echo "Kubeoutput2 : ${KUBE_OUTPUT2}"
                if(KUBE_OUTPUT2.isEmpty()) {
                    echo "Creating missing master-deployment2..."
                }

                sh '/usr/share/google-cloud-sdk/bin/kubectl set image deployment/master-deployment master-service=eu.gcr.io/genuine-axe-182507/master:${BUILD_NUMBER}'
                sh '/usr/share/google-cloud-sdk/bin/kubectl set image deployment/worker-deployment worker=eu.gcr.io/genuine-axe-182507/worker:${BUILD_NUMBER}'
            }
        }
    }
}