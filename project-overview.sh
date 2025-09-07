#!/bin/bash

# Project Overview Script
echo "🔍 Observability Demo Application - Project Overview"
echo "===================================================="
echo

# Function to display file info with description
show_file() {
    local file="$1"
    local description="$2"
    local size=""
    
    if [[ -f "$file" ]]; then
        size=$(wc -l < "$file" 2>/dev/null || echo "0")
        echo "📄 $file ($size lines)"
        echo "   └── $description"
        echo
    elif [[ -d "$file" ]]; then
        local count=$(find "$file" -type f 2>/dev/null | wc -l)
        echo "📁 $file/ ($count files)"
        echo "   └── $description"
        echo
    fi
}

echo "📊 PROJECT STATISTICS:"
echo "======================"
total_java_files=$(find src -name "*.java" 2>/dev/null | wc -l)
total_yaml_files=$(find . -name "*.yml" -o -name "*.yaml" 2>/dev/null | wc -l)
total_json_files=$(find . -name "*.json" 2>/dev/null | wc -l)
total_xml_files=$(find . -name "*.xml" 2>/dev/null | wc -l)
total_md_files=$(find . -name "*.md" 2>/dev/null | wc -l)
total_lines=$(find . -name "*.java" -o -name "*.yml" -o -name "*.yaml" -o -name "*.xml" -o -name "*.json" -o -name "*.md" -o -name "*.sh" 2>/dev/null | xargs wc -l 2>/dev/null | tail -1 | awk '{print $1}')

echo "   • Java files: $total_java_files"
echo "   • YAML files: $total_yaml_files"
echo "   • JSON files: $total_json_files"
echo "   • XML files: $total_xml_files"
echo "   • Markdown files: $total_md_files"
echo "   • Total lines of code: $total_lines"
echo

echo "🏗️ PROJECT STRUCTURE:"
echo "======================"

# Root level files
show_file "README.md" "Complete project documentation with setup instructions, architecture overview, and usage examples"
show_file "GITHUB_SETUP.md" "Step-by-step guide for setting up GitHub repository and collaboration"
show_file "LICENSE" "MIT license for open-source distribution"
show_file "pom.xml" "Maven configuration with all dependencies for observability stack"
show_file ".gitignore" "Git ignore rules for Java/Spring Boot projects"
show_file ".env.example" "Environment variables template for Grafana Cloud and other configurations"
show_file "start-demo.sh" "Quick start script to run the complete demo environment"
show_file "project-overview.sh" "This script - provides complete project overview"

echo

# Docker and containerization
echo "🐳 CONTAINERIZATION & DEPLOYMENT:"
echo "=================================="
show_file "Dockerfile" "Multi-stage Docker build with security best practices"
show_file "docker-compose.yml" "Complete observability stack (Grafana, Prometheus, Loki, Tempo)"
show_file "docker" "Docker configuration files for observability services"
show_file "docker/tempo.yaml" "Tempo configuration for distributed tracing"
show_file "docker/prometheus.yml" "Prometheus configuration for metrics collection"
show_file "docker/grafana" "Grafana provisioning configuration (datasources and dashboards)"

echo

# CI/CD Pipeline
echo "🔄 CI/CD PIPELINE:"
echo "=================="
show_file "Jenkinsfile" "Complete Jenkins pipeline with SonarQube, Trivy, Docker build and security scanning"
show_file "jenkins" "Jenkins pipeline configuration and setup documentation"
show_file "jenkins/pipeline-config.md" "Detailed Jenkins setup guide with plugin requirements"
show_file "owasp-suppressions.xml" "OWASP dependency check suppressions for security scanning"

echo

# Java Application
echo "☕ JAVA APPLICATION:"
echo "===================="
show_file "src" "Java source code directory"
show_file "src/main/java" "Main application code with observability instrumentation"
show_file "src/main/java/com/demo/observability/ObservabilityDemoApplication.java" "Spring Boot main application class"
show_file "src/main/java/com/demo/observability/controller/DemoController.java" "REST controller with metrics, logging, and tracing"
show_file "src/main/java/com/demo/observability/service/DemoService.java" "Business service layer with observability annotations"
show_file "src/main/resources" "Application configuration files"
show_file "src/main/resources/application.yml" "Main Spring Boot configuration"
show_file "src/main/resources/application-prod.yml" "Production configuration with Grafana Cloud integration"
show_file "src/main/resources/logback-spring.xml" "Structured logging configuration with Loki integration"
show_file "src/test/java" "Unit and integration tests"
show_file "src/test/java/com/demo/observability/controller/DemoControllerTest.java" "Controller unit tests with MockMvc"

echo

# Observability and Monitoring
echo "📊 OBSERVABILITY & MONITORING:"
echo "==============================="
show_file "grafana-dashboards" "Pre-built Grafana dashboards for application monitoring"
show_file "grafana-dashboards/application-overview.json" "Comprehensive dashboard with metrics, JVM stats, and custom business metrics"

echo

# Key Features Summary
echo "🚀 KEY FEATURES:"
echo "================"
echo "   ✅ Spring Boot application with REST API"
echo "   ✅ Full observability stack (Metrics, Logs, Traces)"
echo "   ✅ Grafana Cloud integration"
echo "   ✅ Jenkins CI/CD pipeline"
echo "   ✅ Security scanning with SonarQube and Trivy"
echo "   ✅ Docker containerization"
echo "   ✅ Structured JSON logging"
echo "   ✅ Distributed tracing with OpenTelemetry"
echo "   ✅ Custom metrics with Micrometer"
echo "   ✅ Health checks and actuator endpoints"
echo "   ✅ Comprehensive documentation"
echo "   ✅ Quick start automation"
echo

echo "🛠️ OBSERVABILITY STACK COMPONENTS:"
echo "===================================="
echo "   📈 Metrics: Micrometer → Prometheus → Grafana Cloud"
echo "   📝 Logging: Logback → Loki → Grafana Cloud"  
echo "   🔍 Tracing: OpenTelemetry → Tempo → Grafana Cloud"
echo "   📊 Dashboards: Grafana with pre-built dashboards"
echo "   🚨 Alerting: Ready for Grafana Cloud alerting setup"
echo

echo "🔧 CI/CD PIPELINE STAGES:"
echo "=========================="
echo "   1. 📥 Checkout code from Git"
echo "   2. 🔨 Build & Test (Maven + JaCoCo coverage)"
echo "   3. 📊 Code Quality Analysis (SonarQube + OWASP)"
echo "   4. ✅ Quality Gate validation"
echo "   5. 📦 Package application (JAR)"
echo "   6. 🐳 Docker build with multi-stage"
echo "   7. 🔒 Security scan (Trivy + Hadolint)"
echo "   8. 🧪 Integration tests"
echo "   9. 📤 Push to Docker registry"
echo "   10. 🚀 Deploy to staging environment"
echo

echo "📋 QUICK START COMMANDS:"
echo "========================="
echo "   # Start the demo environment"
echo "   ./start-demo.sh"
echo ""
echo "   # Or manually:"
echo "   docker-compose up -d"
echo "   mvn spring-boot:run"
echo ""
echo "   # Run tests"
echo "   mvn test"
echo ""
echo "   # Build Docker image"
echo "   docker build -t observability-demo ."
echo

echo "🌐 ACCESS URLS (when running):"
echo "==============================="
echo "   • Application:     http://localhost:8080"
echo "   • Health Check:    http://localhost:8080/actuator/health"
echo "   • Metrics:         http://localhost:8080/actuator/prometheus"
echo "   • API Endpoints:   http://localhost:8080/api/demo/*"
echo "   • Grafana:         http://localhost:3000 (admin/admin)"
echo "   • Prometheus:      http://localhost:9090"
echo "   • Loki:            http://localhost:3100"
echo

echo "🎯 READY FOR GITHUB:"
echo "===================="
echo "   All files are ready to be pushed to GitHub repository"
echo "   Follow the instructions in GITHUB_SETUP.md"
echo "   Repository will include:"
echo "   • Complete source code"
echo "   • Documentation"
echo "   • CI/CD pipeline"
echo "   • Docker configuration"
echo "   • Observability setup"
echo "   • Quick start scripts"
echo

echo "📞 NEXT STEPS:"
echo "==============="
echo "   1. 📋 Review GITHUB_SETUP.md for repository creation"
echo "   2. 🔧 Configure your Grafana Cloud credentials"
echo "   3. 🏗️ Set up Jenkins pipeline"
echo "   4. 🚀 Deploy to your infrastructure"
echo "   5. 📊 Import Grafana dashboards"
echo "   6. 🚨 Configure alerting rules"
echo

echo "✨ Project ready for GitHub repository creation!"
echo "================================================"
