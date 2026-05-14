pipeline {
    agent any

    // Ensure we don't automatically trigger unless specified, matching your manual requirement
    options {
        disableConcurrentBuilds()
    }

    stages {
        stage('Build') {
            steps {
                echo '--- Starting Build ---'
                // Ensure the wrapper is executable and build the application
                sh 'chmod +x ./mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Deploy & Restart') {
            steps {
                echo '--- Build Successful. Starting Deployment ---'
                
                // Copy the JAR to the deployment folder
                sh 'cp target/quantity-measurement-app-0.0.1-SNAPSHOT.jar /opt/quantity-api/quantity-measurement-app.jar'
                
                // Restart the systemd service
                sh 'sudo systemctl restart quantity-api.service'
                
                echo '--- Deployment Complete ---'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully! The API is live on port 4041.'
        }
        failure {
            echo 'Pipeline failed. Check the stage logs above.'
        }
    }
}
