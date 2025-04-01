pipeline {
    agent any
    
    environment {
        MAVEN_OPTS = "-Dmaven.repo.local=/home/minashehata/.m2/repository"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Test') {
            steps {
                sh 'whoami'  // Debugging step to check user
                sh 'mvn test -Dmaven.repo.local=/home/minashehata/.m2/repository'
            }
            post {
                failure {
                    error "Unit tests failed. Pipeline execution terminated."
                }
            }
        }
        
        stage('Build JAR') {
            steps {
                sh 'mvn package -DskipTests -Dmaven.repo.local=/home/minashehata/.m2/repository'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t xperience-server:latest .'
            }
        }
        
        stage('Deploy') {
            steps {
                sh 'docker stop xperience-server || true'
                sh 'docker rm xperience-server || true'
                sh 'docker run -d -p 8000:8000 --name xperience-server xperience-server:latest'
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline execution failed!'
        }
    }
}
