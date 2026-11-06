pipeline {
    agent any

    triggers {
        pollSCM('* * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn -B clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Code Inspection - SonarQube') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh 'mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184:sonar -Dsonar.projectKey=invoice-service -Dsonar.host.url=http://localhost:9000 -Dsonar.login=$SONAR_TOKEN -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t invoice-service:${BUILD_NUMBER} .'
            }
        }

        stage('Deploy to Staging') {
            steps {
                sh '''
                    docker rm -f invoice-staging || true
                    docker run -d --name invoice-staging -e APP_ENV=staging -p 8085:8080 invoice-service:${BUILD_NUMBER}
                    sleep 5
                    curl -s http://localhost:8085
                '''
            }
        }

        stage('Approval') {
            steps {
                input message: 'Staging verified. Deploy to Production?', ok: 'Deploy'
            }
        }

        stage('Deploy to Production') {
            steps {
                sh '''
                    docker rm -f invoice-prod || true
                    docker run -d --name invoice-prod -e APP_ENV=production -p 8086:8080 invoice-service:${BUILD_NUMBER}
                    sleep 5
                    curl -s http://localhost:8086
                '''
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}
