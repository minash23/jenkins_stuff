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
                sh 'docker stop xperience-server-memory || true'
                sh 'docker rm xperience-server-memory || true'
                sh 'docker stop xperience-server-db || true'
                sh 'docker rm xperience-server-db || true'

                // Run Memory container
                sh 'docker run -d -p 8000:8000 --name xperience-server-memory xperience-server:latest'

                // Run DB container with environment variables
                sh """
                    docker run -d -p 9000:9000 --name xperience-server-db \\
                    -e DB_SERVER=${params.DB_SERVER} \\
                    -e DB_USER=${params.DB_USER} \\
                    -e DB_PASS=${params.DB_PASS} \\
                    xperience-server-db:latest
                """
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline execution failed!'
        }
    }
}

