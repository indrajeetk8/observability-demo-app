# GitHub Repository Setup Guide

This guide will help you create a GitHub repository for your Observability Demo Application and push all the code.

## 📋 Prerequisites

1. **GitHub Account**: Make sure you have a GitHub account
2. **Git CLI**: Git should be installed on your system
3. **GitHub CLI (Optional)**: For easier repository creation

## 🚀 Step-by-Step Setup

### 1. Create GitHub Repository

#### Option A: Using GitHub Web Interface

1. Go to [GitHub](https://github.com)
2. Click the "+" icon in the top right corner
3. Select "New repository"
4. Fill in the details:
   - **Repository name**: `observability-demo-app`
   - **Description**: `Comprehensive Java Spring Boot application with full observability stack (Grafana Cloud, Loki, Tempo, Prometheus) and CI/CD pipeline using Jenkins, SonarQube, Trivy, and Docker`
   - **Visibility**: Public (recommended) or Private
   - **Initialize**: DO NOT initialize with README, .gitignore, or license (we already have these)
5. Click "Create repository"

#### Option B: Using GitHub CLI

```bash
# Install GitHub CLI (if not installed)
# Ubuntu/Debian:
sudo apt install gh

# Login to GitHub
gh auth login

# Create repository
gh repo create observability-demo-app --public --description "Comprehensive Java Spring Boot application with full observability stack"
```

### 2. Configure Git (First Time Setup)

```bash
# Set your Git username and email (if not already set)
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"

# Optional: Set default branch to main
git config --global init.defaultBranch main
```

### 3. Initialize and Push Repository

Run these commands from your project directory (`/home/indrajeet/observability-demo-app`):

```bash
# Navigate to project directory
cd /home/indrajeet/observability-demo-app

# Rename branch to main (if needed)
git branch -M main

# Add all files
git add .

# Commit the initial version
git commit -m "Initial commit: Complete observability demo application

Features:
- Spring Boot application with REST API endpoints
- Full observability stack (Metrics, Logging, Tracing)
- Grafana Cloud integration (Loki, Tempo, Prometheus)
- Jenkins CI/CD pipeline with SonarQube and Trivy
- Docker containerization with security best practices
- Comprehensive documentation and quick start scripts"

# Add remote origin (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/observability-demo-app.git

# Push to GitHub
git push -u origin main
```

### 4. Verify Repository Setup

After pushing, verify that everything is correctly uploaded:

1. Go to your repository on GitHub
2. Check that all files and folders are present
3. Verify the README.md is displayed properly
4. Check that the .gitignore is working (target/ folder should not be present)

## 📁 Repository Structure

Your GitHub repository will contain:

```
observability-demo-app/
├── .github/workflows/          # Future: GitHub Actions (optional)
├── docker/                     # Docker configuration files
│   ├── grafana/               # Grafana provisioning
│   ├── tempo.yaml             # Tempo configuration
│   └── prometheus.yml         # Prometheus configuration
├── grafana-dashboards/        # Pre-built Grafana dashboards
│   └── application-overview.json
├── jenkins/                   # Jenkins pipeline documentation
│   └── pipeline-config.md
├── src/                       # Java source code
│   ├── main/java/             # Application code
│   ├── main/resources/        # Configuration files
│   └── test/java/             # Test files
├── .env.example               # Environment variables template
├── .gitignore                 # Git ignore rules
├── docker-compose.yml         # Local development stack
├── Dockerfile                 # Application container
├── GITHUB_SETUP.md           # This setup guide
├── Jenkinsfile               # Jenkins pipeline definition
├── owasp-suppressions.xml    # OWASP dependency check suppressions
├── pom.xml                   # Maven configuration
├── README.md                 # Main documentation
└── start-demo.sh             # Quick start script
```

## 🔧 Repository Configuration

### Branch Protection (Recommended for Production)

1. Go to your repository settings
2. Navigate to "Branches"
3. Add rule for `main` branch:
   - Require pull request reviews
   - Require status checks to pass
   - Restrict pushes to main branch

### Secrets Configuration (For CI/CD)

If you plan to use GitHub Actions or need to store secrets:

1. Go to repository "Settings" → "Secrets and variables" → "Actions"
2. Add the following secrets:
   - `DOCKER_USERNAME`: Your Docker Hub username
   - `DOCKER_PASSWORD`: Your Docker Hub password/token
   - `SONAR_TOKEN`: SonarQube/SonarCloud token
   - `GRAFANA_CLOUD_API_KEY`: Your Grafana Cloud API key

### Repository Topics (For Discoverability)

Add topics to your repository:
- `spring-boot`
- `observability`
- `grafana`
- `prometheus`
- `loki`
- `tempo`
- `jenkins`
- `docker`
- `java`
- `microservices`
- `monitoring`
- `logging`
- `tracing`
- `cicd`

## 📝 Clone Instructions for Others

Once your repository is public, others can clone it using:

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/observability-demo-app.git

# Navigate to project directory
cd observability-demo-app

# Start the demo
./start-demo.sh
```

## 🔄 Making Changes

For future updates:

```bash
# Make your changes

# Add modified files
git add .

# Commit changes
git commit -m "Description of changes"

# Push to GitHub
git push origin main
```

## 🏷️ Creating Releases

Create releases for major versions:

```bash
# Create and push a tag
git tag -a v1.0.0 -m "Initial release with full observability stack"
git push origin v1.0.0
```

Then create a release on GitHub using the web interface.

## 🤝 Collaboration

To allow others to contribute:

1. **Fork**: Others fork your repository
2. **Branch**: Create feature branches
3. **Pull Request**: Submit changes via PR
4. **Review**: Review and merge changes

## 📞 Support

If you encounter issues:

1. Check the main [README.md](./README.md) for troubleshooting
2. Review the [Jenkins setup guide](./jenkins/pipeline-config.md)
3. Open an issue on the GitHub repository
4. Check existing issues for similar problems

---

**Repository URL Template**: `https://github.com/YOUR_USERNAME/observability-demo-app`

Replace `YOUR_USERNAME` with your actual GitHub username throughout this guide.
