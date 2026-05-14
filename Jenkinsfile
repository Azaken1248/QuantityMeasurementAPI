pipeline {
    agent any

    options {
        disableConcurrentBuilds()
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Build') {
            steps {
                echo '--- Starting Build ---'
                sh 'chmod +x ./mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Deploy & Restart') {
            steps {
                echo '--- Build Successful. Starting Deployment ---'
                
                sh 'cp target/quantity-measurement-app-0.0.1-SNAPSHOT.jar /opt/quantity-api/quantity-measurement-app.jar'
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
