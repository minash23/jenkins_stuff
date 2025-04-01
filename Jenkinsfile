pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                // Fetch code from Git repository
                checkout scm
            }
        }
        
        stage('Test') {
            steps {
                // Run tests using Maven
                sh 'mvn test'
            }
            post {
                failure {
                    // If tests fail, terminate with error message
                    error "Unit tests failed. Pipeline execution terminated."
                }
            }
        }
        
        stage('Build JAR') {
            steps {
                // Build the JAR file
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                // Build Docker image
                sh 'docker build -t xperience-server:latest .'
            }
        }
        
        stage('Deploy') {
            steps {
                // Stop and remove any existing container
                sh 'docker stop xperience-server || true'
                sh 'docker rm xperience-server || true'
                
                // Run the new container
                sh 'docker run -d -p 8000:8000 --name xperience-server xperience-server:latest'
            }
        }
    }
    
    post {
        always {
            // Clean up workspace
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
