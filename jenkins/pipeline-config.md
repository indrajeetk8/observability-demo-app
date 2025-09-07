# Jenkins Pipeline Configuration Guide

## Prerequisites

Before running the Jenkins pipeline, ensure the following tools and plugins are installed:

### Required Jenkins Plugins
1. **Pipeline Plugin** - For pipeline as code
2. **SonarQube Scanner Plugin** - For code quality analysis
3. **Docker Plugin** - For Docker operations
4. **HTML Publisher Plugin** - For publishing reports
5. **Email Extension Plugin** - For notifications
6. **JUnit Plugin** - For test result publishing
7. **Jacoco Plugin** - For coverage reports

### Required Tools on Jenkins Agent
1. **Java 17** - Set as JDK-17 in Global Tool Configuration
2. **Maven 3.9.5** - Set as Maven-3.9.5 in Global Tool Configuration
3. **Docker** - For building and running containers
4. **Git** - For source code management

## Jenkins Global Configuration

### 1. SonarQube Configuration
1. Go to **Manage Jenkins > Configure System**
2. Find **SonarQube servers** section
3. Add SonarQube server:
   - Name: `SonarQube`
   - Server URL: `https://sonarcloud.io` (or your SonarQube server URL)
   - Server authentication token: Add SonarQube token in Jenkins credentials

### 2. Docker Registry Configuration
1. Go to **Manage Jenkins > Manage Credentials**
2. Add Docker registry credentials:
   - ID: `docker-registry-credentials`
   - Username: Your registry username
   - Password: Your registry password/token

### 3. Global Tool Configuration
1. Go to **Manage Jenkins > Global Tool Configuration**
2. Configure JDK:
   - Name: `JDK-17`
   - Install automatically or provide JAVA_HOME path
3. Configure Maven:
   - Name: `Maven-3.9.5`
   - Install automatically or provide Maven home path

## Environment Variables

Set the following environment variables in Jenkins:

| Variable | Description | Example |
|----------|-------------|---------|
| `DOCKER_REGISTRY` | Docker registry URL | `your-registry.com` |
| `DOCKER_REPOSITORY` | Repository name | `observability-demo` |
| `SONAR_PROJECT_KEY` | SonarQube project key | `observability-demo` |
| `SONAR_PROJECT_NAME` | SonarQube project name | `Observability Demo` |
| `NOTIFICATION_EMAIL` | Email for notifications | `dev-team@company.com` |

## Pipeline Job Setup

1. **Create New Item**
   - Choose "Pipeline" job type
   - Enter job name: `observability-demo-pipeline`

2. **Pipeline Configuration**
   - Definition: "Pipeline script from SCM"
   - SCM: Git
   - Repository URL: Your Git repository URL
   - Script Path: `Jenkinsfile`

3. **Build Triggers** (Optional)
   - GitHub hook trigger for GITScm polling
   - Poll SCM: `H/15 * * * *` (every 15 minutes)

## Pipeline Stages Explained

### 1. Checkout
- Automatically handled by Jenkins SCM integration
- Pulls latest code from the configured Git repository

### 2. Build & Test
- Compiles Java code using Maven
- Runs unit tests with JaCoCo coverage
- Publishes test results and coverage reports

### 3. Code Quality Analysis (Parallel)
- **SonarQube Analysis**: Code quality and security analysis
- **OWASP Dependency Check**: Security vulnerability scanning

### 4. Quality Gate
- Waits for SonarQube quality gate results
- Fails pipeline if quality gate fails

### 5. Package Application
- Creates JAR file using Maven
- Archives artifacts for deployment

### 6. Docker Build
- Builds Docker image using Dockerfile
- Tags image with build number and latest

### 7. Security Scan (Parallel)
- **Trivy Container Scan**: Scans Docker image for vulnerabilities
- **Container Best Practices**: Checks Dockerfile with Hadolint

### 8. Integration Tests
- Runs container with test configuration
- Performs health checks and API tests

### 9. Push to Registry
- Pushes Docker images to registry
- Only runs on main/master/develop branches

### 10. Deploy to Staging
- Placeholder for deployment commands
- Only runs on main/master/develop branches

## Customization Options

### Branch-based Deployment
The pipeline includes branch-based conditions:
```groovy
when {
    anyOf {
        branch 'main'
        branch 'master'
        branch 'develop'
    }
}
```

### Security Scan Thresholds
Modify security scan behavior:
```groovy
# Fail build on critical vulnerabilities
if [ "$CRITICAL_COUNT" -gt 5 ]; then
    exit 1  # Uncomment to fail build
fi
```

### Notification Configuration
Update notification settings in the `post` section:
```groovy
emailext (
    subject: "Pipeline Status: ${env.JOB_NAME}",
    body: "Pipeline details...",
    to: "${env.NOTIFICATION_EMAIL}"
)
```

## Troubleshooting

### Common Issues

1. **SonarQube Connection Failed**
   - Verify SonarQube server configuration
   - Check authentication token
   - Ensure network connectivity

2. **Docker Build Failed**
   - Verify Docker daemon is running
   - Check Dockerfile syntax
   - Ensure base images are accessible

3. **Trivy Installation Failed**
   - Check internet connectivity
   - Verify sudo permissions for Jenkins user
   - Consider pre-installing Trivy on Jenkins agents

4. **Test Failures**
   - Check application logs
   - Verify database connectivity
   - Review test configuration

### Performance Optimization

1. **Use Docker Build Cache**
   ```dockerfile
   # Add to Dockerfile for better caching
   COPY pom.xml .
   RUN mvn dependency:go-offline
   ```

2. **Parallel Execution**
   - The pipeline already uses parallel stages for security scans
   - Consider adding more parallel stages for faster execution

3. **Agent Labels**
   ```groovy
   agent {
       label 'docker-enabled'
   }
   ```

## Monitoring Pipeline Performance

### Key Metrics to Track
- Build duration
- Test success rate
- Code coverage percentage
- Security vulnerabilities count
- Deployment frequency

### Jenkins Blue Ocean
Consider using Blue Ocean plugin for better pipeline visualization and monitoring.
