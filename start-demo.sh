#!/bin/bash

# Observability Demo - Quick Start Script
echo "🚀 Starting Observability Demo Application"
echo "=========================================="

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17+ first."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.6+ first."
    exit 1
fi

echo "✅ All prerequisites are available"
echo

# Build the application
echo "🔨 Building the application..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Build failed. Please check the error messages above."
    exit 1
fi

echo "✅ Application built successfully"
echo

# Start the observability stack
echo "🐳 Starting observability stack (Grafana, Prometheus, Loki, Tempo)..."
docker-compose up -d
if [ $? -ne 0 ]; then
    echo "❌ Failed to start observability stack. Please check Docker Compose logs."
    exit 1
fi

echo "✅ Observability stack started successfully"
echo

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 30

# Start the application
echo "🏃 Starting the Spring Boot application..."
mvn spring-boot:run &
APP_PID=$!

# Wait for application to start
echo "⏳ Waiting for application to be ready..."
sleep 15

# Test if application is running
if curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; then
    echo "✅ Application is running successfully!"
else
    echo "⚠️  Application might still be starting. Please wait a moment and try the URLs below."
fi

echo
echo "🎉 Demo Setup Complete!"
echo "======================"
echo
echo "📱 Application URLs:"
echo "   • Application:     http://localhost:8080"
echo "   • Health Check:    http://localhost:8080/actuator/health"
echo "   • Metrics:         http://localhost:8080/actuator/prometheus"
echo "   • API Demo:        http://localhost:8080/api/demo/health"
echo
echo "📊 Observability Stack URLs:"
echo "   • Grafana:         http://localhost:3000 (admin/admin)"
echo "   • Prometheus:      http://localhost:9090"
echo "   • Loki:            http://localhost:3100"
echo
echo "🧪 Test the API:"
echo "   curl http://localhost:8080/api/demo/health"
echo "   curl http://localhost:8080/api/demo/slow"
echo "   curl http://localhost:8080/api/demo/error"
echo "   curl -X POST http://localhost:8080/api/demo/users -H 'Content-Type: application/json' -d '{\"name\":\"Test User\",\"email\":\"test@example.com\"}'"
echo
echo "📝 Next Steps:"
echo "   1. Import the Grafana dashboard from grafana-dashboards/application-overview.json"
echo "   2. Configure your Grafana Cloud credentials in .env file"
echo "   3. Set up Jenkins pipeline using the Jenkinsfile"
echo "   4. Run tests: mvn test"
echo
echo "🛑 To stop everything:"
echo "   Press Ctrl+C to stop the application, then run:"
echo "   docker-compose down"
echo

# Keep the script running to show the application PID
echo "Application PID: $APP_PID"
echo "Press Ctrl+C to stop the application"

# Wait for Ctrl+C
trap 'echo "Stopping application..."; kill $APP_PID; docker-compose down; exit 0' INT
wait $APP_PID
