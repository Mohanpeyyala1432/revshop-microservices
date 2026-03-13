pipeline {
    agent any


    stages {
        stage('Checkout') {
            steps {
                // Pull the latest code
                checkout scm
            }
        }

        stage('Build Core Services') {
            steps {
                script {
                    def services = ['eureka-server', 'config-server', 'api-gateway']
                    for (service in services) {
                        echo "Building ${service}..."
                        dir(service) {
                            bat 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Build Business Services') {
            steps {
                script {
                    def services = ['user-service', 'product-service', 'cart-service', 'order-service', 'payment-service', 'notification-service']
                    for (service in services) {
                        echo "Building ${service}..."
                        dir(service) {
                            bat 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Build Frontend') {
            steps {
                echo "Building Frontend..."
                dir('revshop-frontend') {
                    bat 'npm install'
                    bat 'npm run build'
                }
            }
        }

/*
        stage('SonarQube Analysis') {
            steps {
                script {
                    // Use the existing sonar scanner tool defined in Jenkins global config
                    withSonarQubeEnv('SonarQube') {
                        // Scan using the root sonar-project.properties
                        bat 'mvn sonar:sonar'
                    }
                }
            }
        }
*/

        stage('Docker Build & Package') {
            steps {
                script {
                    def allServices = [
                        'eureka-server', 'config-server', 
                        'api-gateway', 'user-service', 'product-service', 
                        'cart-service', 'order-service', 'payment-service', 
                        'notification-service', 'revshop-frontend'
                    ]
                    echo "Building Docker images one by one to save memory..."
                    for (service in allServices) {
                        echo "Building image for ${service}..."
                        bat "docker-compose build ${service}"
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Build and Dockerization completed successfully!'
        }
        failure {
            echo 'Build failed. Please check the logs.'
        }
    }
}
