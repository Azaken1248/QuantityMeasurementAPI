pipeline {
    agent any

    options {
        disableConcurrentBuilds()
    }

    stages {
        stage('Clean Workspace') {
            steps {
                echo '--- Nuking old target directory to prevent permission errors ---'
                // If root locked the folder from a previous manual run, this forces it open.
                // The || true ensures the pipeline doesn't crash if the folder doesn't exist yet.
                sh 'sudo rm -rf target/ || true'
            }
        }

        stage('Build') {
            steps {
                echo '--- Starting Build ---'
                
                // Using a multi-line shell script to brutally enforce Java 17 for Maven
                sh '''
                    export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
                    export PATH=$JAVA_HOME/bin:$PATH
                    
                    echo "--- Verifying Java Version ---"
                    java -version
                    
                    chmod +x ./mvnw
                    ./mvnw clean package -DskipTests
                '''
            }
        }

        stage('Deploy & Restart') {
            steps {
                echo '--- Build Successful. Starting Deployment ---'
                
                // Enforced sudo on the copy command just to be absolutely safe
                sh 'sudo cp target/quantity-measurement-app-0.0.1-SNAPSHOT.jar /opt/quantity-api/quantity-measurement-app.jar'
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
