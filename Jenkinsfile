pipeline {
  agent any
  options {
    timestamps()
  }
  tools {
    maven 'Maven-3.9.5'
    // Match pom.xml which uses Java 11
    jdk 'JDK-11'
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
          set -euxo pipefail
          mvn --version
          java -version || true

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
          set -euxo pipefail
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
        sh '''
          set -euxo pipefail
          IMAGE_REF="${DOCKER_REPOSITORY}:${IMAGE_TAG}"
          if [ -n "${DOCKER_REGISTRY}" ]; then IMAGE_REF="${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}:${IMAGE_TAG}"; fi
          echo "Building ${IMAGE_REF}"
          docker build -t "${IMAGE_REF}" .
          echo "${IMAGE_REF}" > .image_ref
          docker images | grep "${DOCKER_REPOSITORY}" || true
        '''
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
      sh 'docker system prune -f || true'
    }
  }
}
