pipeline {
  agent any
  options {
    timestamps()
  }
  environment {
    APP_NAME = 'observability-demo'
    IMAGE_TAG = "${env.BUILD_NUMBER}"
    // Set these in the job or leave empty to skip push
    DOCKER_REGISTRY = "${env.DOCKER_REGISTRY ?: ''}"
    DOCKER_REPOSITORY = "${env.DOCKER_REPOSITORY ?: 'observability-demo'}"
    DOCKER_CREDENTIALS_ID = "${env.DOCKER_CREDENTIALS_ID ?: ''}"
  }
  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        sh '''
          set -eux
          
          # Check if Maven is available
          if ! command -v mvn >/dev/null 2>&1; then
            echo "Maven not found in PATH. Trying to find it..."
            export PATH="/opt/maven/bin:/usr/share/maven/bin:$PATH"
          fi
          
          # Check versions
          mvn --version || echo "Maven not available"
          java -version || echo "Java not available"
          
          # Clean, compile and run tests
          mvn -B clean verify
        '''
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
        }
      }
    }

    stage('Package') {
      steps {
        sh '''
          set -eux
          mvn -B -DskipTests package
          ls -la target
        '''
      }
      post {
        always {
          archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
      }
    }

    stage('Docker Build') {
      when {
        expression { return fileExists('Dockerfile') && (sh(script: 'command -v docker >/dev/null 2>&1', returnStatus: true) == 0) }
      }
      steps {
        script {
          // Handle environment variables safely
          def dockerRegistry = env.DOCKER_REGISTRY ?: ''
          def dockerRepository = env.DOCKER_REPOSITORY ?: 'observability-demo'
          def imageTag = env.BUILD_NUMBER
          
          // Build image reference
          def imageRef
          if (dockerRegistry && dockerRegistry.trim()) {
            imageRef = "${dockerRegistry}/${dockerRepository}:${imageTag}"
            echo "Building image with registry: ${imageRef}"
          } else {
            imageRef = "${dockerRepository}:${imageTag}"
            echo "Building local image: ${imageRef}"
          }
          
          // Build Docker image
          sh """
            set -eux
            echo "Building Docker image: ${imageRef}"
            docker build -t "${imageRef}" .
            echo "${imageRef}" > .image_ref
            docker images | grep "${dockerRepository}" || echo "No images found with repository name ${dockerRepository}"
            echo "Docker build completed successfully!"
          """
        }
      }
    }

    stage('Push Image') {
      when {
        expression {
          return fileExists('.image_ref') && env.DOCKER_REGISTRY?.trim() && env.DOCKER_CREDENTIALS_ID?.trim()
        }
      }
      steps {
        script {
          def imageRef = readFile('.image_ref').trim()
          withDockerRegistry(credentialsId: "${DOCKER_CREDENTIALS_ID}", url: "https://${DOCKER_REGISTRY}") {
            sh "docker push ${imageRef}"
          }
        }
      }
    }
  }
  post {
    always {
      echo "Pipeline completed"
    }
  }
}
