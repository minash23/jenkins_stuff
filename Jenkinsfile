pipeline {
    agent any
    
    environment {
        MAVEN_OPTS = "-Dmaven.repo.local=/home/minashehata/.m2/repository"
        DB_SERVER = "10.0.2.17"
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
        
        stage('Build Docker Images') {
            parallel {
                stage('Build Memory Image') {
                    steps {
                        sh 'docker build -t xperience-server-memory:latest -f DockerFileMemory .'
                    }
                }
                stage('Build DB Image') {
                    steps {
                        sh 'docker build -t xperience-server-db:latest -f DockerFileDB --build-arg DB_SERVER=${DB_SERVER} .'
                    }
                }
            }
        }
        
        stage('Deploy Containers') {
            parallel {
                stage('Deploy Memory Container') {
                    steps {
                        sh '''
                            docker stop xperience-memory || true
                            docker rm xperience-memory || true
                            docker run -d -p 8000:8000 --name xperience-memory xperience-server-memory:latest
                        '''
                    }
                }
                stage('Deploy DB Container') {
                    steps {
                        sh '''
                            docker stop xperience-db || true
                            docker rm xperience-db || true
                            docker run -d -p 9000:9000 --name xperience-db xperience-server-db:latest
                        '''
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo 'Both memory and DB containers deployed successfully!'
        }
        failure {
            echo 'Pipeline execution failed!'
        }
    }
}
