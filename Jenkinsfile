pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9.5'
        jdk 'JDK-17'
    }
    
    environment {
        DOCKER_REGISTRY = "${env.DOCKER_REGISTRY ?: 'your-registry.com'}"
        DOCKER_REPOSITORY = "${env.DOCKER_REPOSITORY ?: 'observability-demo'}"
        SONAR_PROJECT_KEY = "${env.SONAR_PROJECT_KEY ?: 'observability-demo'}"
        SONAR_PROJECT_NAME = "${env.SONAR_PROJECT_NAME ?: 'Observability Demo'}"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        LATEST_TAG = "latest"
    }
    
    stages {
        stage('Checkout') {
            steps {
                script {
                    echo "Checking out code from Git repository"
                    // Git checkout is automatically handled by Jenkins when using SCM
                }
            }
        }
        
        stage('Build & Test') {
            steps {
                script {
                    echo "Building and testing the application"
                    sh '''
                        echo "Maven Version:"
                        mvn --version
                        echo "Java Version:"
                        java -version
                        
                        # Clean and compile
                        mvn clean compile
                        
                        # Run tests with coverage
                        mvn test jacoco:report
                    '''
                }
            }
            post {
                always {
                    // Publish test results
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    
                    // Publish coverage results
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
                }
            }
        }
        
        stage('Code Quality Analysis') {
            parallel {
                stage('SonarQube Analysis') {
                    steps {
                        script {
                            echo "Running SonarQube analysis"
                            withSonarQubeEnv('SonarQube') {
                                sh '''
                                    mvn sonar:sonar \
                                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                                        -Dsonar.projectName="${SONAR_PROJECT_NAME}" \
                                        -Dsonar.java.coveragePlugin=jacoco \
                                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                                        -Dsonar.junit.reportPaths=target/surefire-reports \
                                        -Dsonar.surefire.reportsPath=target/surefire-reports
                                '''
                            }
                        }
                    }
                }
                
                stage('OWASP Dependency Check') {
                    steps {
                        script {
                            echo "Running OWASP Dependency Check"
                            sh '''
                                mvn org.owasp:dependency-check-maven:check \
                                    -DfailBuildOnCVSS=7 \
                                    -DsuppressionFiles=owasp-suppressions.xml
                            '''
                        }
                    }
                    post {
                        always {
                            publishHTML([
                                allowMissing: true,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'target',
                                reportFiles: 'dependency-check-report.html',
                                reportName: 'OWASP Dependency Check Report'
                            ])
                        }
                    }
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                script {
                    echo "Waiting for SonarQube Quality Gate"
                    timeout(time: 5, unit: 'MINUTES') {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qg.status}"
                        }
                    }
                }
            }
        }
        
        stage('Package Application') {
            steps {
                script {
                    echo "Packaging the application"
                    sh '''
                        # Create JAR file
                        mvn package -DskipTests
                        
                        # List the generated artifacts
                        ls -la target/
                    '''
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                script {
                    echo "Building Docker image"
                    sh '''
                        # Build Docker image with multiple tags
                        docker build -t ${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}:${IMAGE_TAG} .
                        docker tag ${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}:${IMAGE_TAG} ${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}:${LATEST_TAG}
                        
                        # List Docker images
                        docker images | grep ${DOCKER_REPOSITORY}
                    '''
                }
            }
        }
        
        stage('Security Scan') {
            parallel {
                stage('Trivy Container Scan') {
                    steps {
                        script {
                            echo "Running Trivy container security scan"
                            sh '''
                                # Install Trivy if not available
                                if ! command -v trivy &> /dev/null; then
                                    echo "Installing Trivy..."
                                    sudo apt-get update
                                    sudo apt-get install wget apt-transport-https gnupg lsb-release
                                    wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | sudo apt-key add -
                                    echo "deb https://aquasecurity.github.io/trivy-repo/deb $(lsb_release -sc) main" | sudo tee -a /etc/apt/sources.list.d/trivy.list
                                    sudo apt-get update
                                    sudo apt-get install trivy
                                fi
                                
                                # Run Trivy scan
                                trivy image --format json --output trivy-report.json ${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}:${IMAGE_TAG}
                                trivy image --format table ${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}:${IMAGE_TAG}
                                
                                # Check for critical vulnerabilities
                                CRITICAL_COUNT=$(cat trivy-report.json | jq '.Results[]?.Vulnerabilities[]? | select(.Severity=="CRITICAL") | .VulnerabilityID' | wc -l)
                                echo "Critical vulnerabilities found: $CRITICAL_COUNT"
                                
                                if [ "$CRITICAL_COUNT" -gt 5 ]; then
                                    echo "WARNING: High number of critical vulnerabilities found!"
                                    # Uncomment the next line to fail the build on critical vulnerabilities
                                    # exit 1
                                fi
                            '''
                        }
                    }
                    post {
                        always {
                            archiveArtifacts artifacts: 'trivy-report.json', allowEmptyArchive: true
                            publishHTML([
                                allowMissing: true,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: '.',
                                reportFiles: 'trivy-report.json',
                                reportName: 'Trivy Security Report'
                            ])
                        }
                    }
                }
                
                stage('Container Best Practices') {
                    steps {
                        script {
                            echo "Checking container best practices with Hadolint"
                            sh '''
                                # Install Hadolint if not available
                                if ! command -v hadolint &> /dev/null; then
                                    echo "Installing Hadolint..."
                                    wget -O hadolint https://github.com/hadolint/hadolint/releases/download/v2.12.0/hadolint-Linux-x86_64
                                    chmod +x hadolint
                                    sudo mv hadolint /usr/local/bin/
                                fi
                                
                                # Run Hadolint on Dockerfile
                                hadolint Dockerfile --format json > hadolint-report.json || true
                                hadolint Dockerfile || true
                            '''
                        }
                    }
                    post {
                        always {
                            archiveArtifacts artifacts: 'hadolint-report.json', allowEmptyArchive: true
                        }
                    }
                }
            }
        }
        
        stage('Integration Tests') {
            steps {
                script {
                    echo "Running integration tests with Docker container"
                    sh '''
                        # Start the container for testing
                        docker run -d --name test-container -p 8081:8080 \
                            -e SPRING_PROFILES_ACTIVE=test \
                            ${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}:${IMAGE_TAG}
                        
                        # Wait for application to start
                        echo "Waiting for application to start..."
                        sleep 30
                        
                        # Run health checks
                        for i in {1..10}; do
                            if curl -f http://localhost:8081/actuator/health; then
                                echo "Application is healthy!"
                                break
                            else
                                echo "Attempt $i: Application not ready yet, waiting..."
                                sleep 10
                            fi
                        done
                        
                        # Run integration tests
                        curl -f http://localhost:8081/api/demo/health || exit 1
                        curl -f http://localhost:8081/actuator/prometheus || exit 1
                        
                        echo "Integration tests passed!"
                    '''
                }
            }
            post {
                always {
                    sh '''
                        # Clean up test container
                        docker stop test-container || true
                        docker rm test-container || true
                    '''
                }
            }
        }
        
        stage('Push to Registry') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                    branch 'develop'
                }
            }
            steps {
                script {
                    echo "Pushing Docker image to registry"
                    withDockerRegistry(credentialsId: 'docker-registry-credentials', url: "https://${DOCKER_REGISTRY}") {
                        sh '''
                            # Push both tags
                            docker push ${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}:${IMAGE_TAG}
                            docker push ${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}:${LATEST_TAG}
                            
                            echo "Successfully pushed images:"
                            echo "  - ${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}:${IMAGE_TAG}"
                            echo "  - ${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}:${LATEST_TAG}"
                        '''
                    }
                }
            }
        }
        
        stage('Deploy to Staging') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                    branch 'develop'
                }
            }
            steps {
                script {
                    echo "Deploying to staging environment"
                    sh '''
                        # Deploy using docker-compose or Kubernetes
                        # This is a placeholder - replace with your actual deployment method
                        
                        echo "Deployment commands would go here..."
                        echo "For example:"
                        echo "  - Update Kubernetes deployment"
                        echo "  - Update docker-compose.yml with new image tag"
                        echo "  - Run deployment scripts"
                        
                        # Example deployment command:
                        # kubectl set image deployment/observability-demo app=${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}:${IMAGE_TAG}
                    '''
                }
            }
        }
    }
    
    post {
        always {
            echo "Pipeline execution completed"
            
            // Clean up Docker images to save space
            sh '''
                docker rmi ${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}:${IMAGE_TAG} || true
                docker system prune -f || true
            '''
        }
        
        success {
            echo "Pipeline executed successfully!"
            // Send notification on success
            emailext (
                subject: "✅ Pipeline Success: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: """
                    Pipeline executed successfully!
                    
                    Job: ${env.JOB_NAME}
                    Build Number: ${env.BUILD_NUMBER}
                    Build URL: ${env.BUILD_URL}
                    
                    Docker Image: ${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}:${IMAGE_TAG}
                """,
                to: "${env.NOTIFICATION_EMAIL ?: 'dev-team@company.com'}"
            )
        }
        
        failure {
            echo "Pipeline failed!"
            // Send notification on failure
            emailext (
                subject: "❌ Pipeline Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: """
                    Pipeline execution failed!
                    
                    Job: ${env.JOB_NAME}
                    Build Number: ${env.BUILD_NUMBER}
                    Build URL: ${env.BUILD_URL}
                    
                    Please check the logs for more details.
                """,
                to: "${env.NOTIFICATION_EMAIL ?: 'dev-team@company.com'}"
            )
        }
        
        unstable {
            echo "Pipeline completed with warnings"
        }
    }
}
