# CI/CD Pipeline Setup Guide - Docker Edition

## 🎉 Installation Complete!

All CI/CD tools have been successfully installed using Docker containers. Your complete observability and CI/CD pipeline is now ready!

## 🚀 Services Running

### CI/CD Tools
- **Jenkins**: http://localhost:8081 (CI/CD Pipeline)
- **SonarQube**: http://localhost:9000 (Code Quality Analysis)
- **CI/CD Tools Container**: All development tools (Java 17, Maven, Trivy, etc.)

### Observability Stack
- **Application**: http://localhost:8080
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090
- **Loki**: http://localhost:3100

### Central Dashboard
- **Nginx Dashboard**: http://localhost (Provides links to all services)

## 📋 What's Installed

### CI/CD Tools Container (`cicd-tools`)
- ✅ **Java 17** (OpenJDK)
- ✅ **Maven 3.9.5** (Build tool)
- ✅ **Docker CLI** (Container operations)
- ✅ **Trivy** (Security scanner)
- ✅ **SonarQube Scanner** (Code quality)
- ✅ **Hadolint** (Dockerfile linter)
- ✅ **Git, curl, wget, unzip** (Basic utilities)

### Services
- ✅ **Jenkins** with required plugins
- ✅ **SonarQube** with PostgreSQL database
- ✅ **Complete Observability Stack** (Grafana, Prometheus, Loki, Tempo)
- ✅ **Nginx Reverse Proxy** for easy access

## 🛠️ Quick Start Commands

### Start All Services
```bash
docker-compose -f docker-compose.cicd.yml up -d
```

### Stop All Services
```bash
docker-compose -f docker-compose.cicd.yml down
```

### View Service Status
```bash
docker-compose -f docker-compose.cicd.yml ps
```

### Access CI/CD Tools Container
```bash
docker exec -it cicd-tools bash
```

### Run Maven Commands
```bash
# Inside the cicd-tools container
docker exec -it cicd-tools mvn clean compile test
```

### Run Security Scans
```bash
# Inside the cicd-tools container
docker exec -it cicd-tools trivy image your-image:tag
```

## 📖 Initial Setup Steps

### 1. Jenkins Setup
1. Open http://localhost:8081
2. Get initial admin password:
   ```bash
   docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```
3. Install suggested plugins
4. Create admin user
5. Configure tools:
   - Add JDK-17 (auto-install or use `/usr/lib/jvm/java-17-openjdk-amd64`)
   - Add Maven-3.9.5 (auto-install or use `/opt/maven`)

### 2. SonarQube Setup
1. Open http://localhost:9000
2. Default login: `admin/admin`
3. Change default password
4. Create new project
5. Generate authentication token
6. Configure Jenkins integration

### 3. Pipeline Configuration
1. Create new Pipeline job in Jenkins
2. Configure Git repository
3. Use `Jenkinsfile` from your project
4. Set environment variables:
   - `DOCKER_REGISTRY`
   - `SONAR_PROJECT_KEY`
   - `NOTIFICATION_EMAIL`

## 🔧 Environment Variables

Set these in Jenkins Global Configuration or Pipeline:

```bash
DOCKER_REGISTRY=localhost:5000          # Your Docker registry
DOCKER_REPOSITORY=observability-demo    # Repository name
SONAR_PROJECT_KEY=observability-demo    # SonarQube project key
SONAR_PROJECT_NAME=Observability Demo   # SonarQube project name
NOTIFICATION_EMAIL=your-email@domain.com # Email for notifications
```

## 🔒 Security Features

- **Container Security**: Trivy vulnerability scanning
- **Code Quality**: SonarQube analysis with quality gates
- **Dockerfile Best Practices**: Hadolint linting
- **OWASP Dependency Check**: Vulnerability detection in dependencies
- **Non-root Users**: Security-hardened containers

## 📊 Pipeline Stages

Your Jenkins pipeline includes:

1. **Checkout** - Source code retrieval
2. **Build & Test** - Maven compilation and unit tests
3. **Code Quality Analysis** - SonarQube + OWASP checks (parallel)
4. **Quality Gate** - Automated quality validation
5. **Package Application** - JAR file creation
6. **Docker Build** - Container image creation
7. **Security Scan** - Trivy + Hadolint (parallel)
8. **Integration Tests** - Container-based testing
9. **Push to Registry** - Image deployment (on main/master/develop)
10. **Deploy to Staging** - Automated deployment (on main/master/develop)

## 🚨 Troubleshooting

### Services Not Starting
```bash
# Check logs
docker-compose -f docker-compose.cicd.yml logs [service-name]

# Restart specific service
docker-compose -f docker-compose.cicd.yml restart [service-name]
```

### Jenkins Initial Password
```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### SonarQube Database Issues
```bash
# Check SonarQube logs
docker logs sonarqube

# Restart with clean database
docker-compose -f docker-compose.cicd.yml down -v
docker-compose -f docker-compose.cicd.yml up -d
```

### CI/CD Tools Container Access
```bash
# Interactive shell
docker exec -it cicd-tools bash

# Check installed tools
docker exec cicd-tools java -version
docker exec cicd-tools mvn -version
docker exec cicd-tools trivy version
```

## 📂 File Structure

```
observability-demo-app/
├── docker-compose.cicd.yml      # Complete CI/CD stack
├── ci-tools.Dockerfile          # CI/CD tools container
├── Jenkinsfile                  # Pipeline definition
├── jenkins/
│   └── plugins.txt             # Required Jenkins plugins
├── nginx/
│   └── nginx.conf              # Reverse proxy config
└── CICD_SETUP_GUIDE.md         # This guide
```

## 🎯 Next Steps

1. **Configure Jenkins**: Set up your first pipeline job
2. **Set up SonarQube**: Create project and quality profiles
3. **Configure Notifications**: Set up email/Slack integrations
4. **Create Git Webhooks**: Enable automatic pipeline triggers
5. **Set up Monitoring**: Use Grafana dashboards for pipeline metrics

## 📧 Support

- **Jenkins Documentation**: https://www.jenkins.io/doc/
- **SonarQube Documentation**: https://docs.sonarqube.org/
- **Docker Documentation**: https://docs.docker.com/
- **Project Issues**: Check GitHub issues or discussions

---

**🎉 Your CI/CD pipeline is ready! Start building amazing software with confidence!**
