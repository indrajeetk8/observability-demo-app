FROM ubuntu:22.04

# Avoid interactive prompts during package installation
ENV DEBIAN_FRONTEND=noninteractive

# Install basic tools
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    unzip \
    git \
    ca-certificates \
    gnupg \
    software-properties-common \
    apt-transport-https \
    lsb-release \
    && rm -rf /var/lib/apt/lists/*

# Install Java 17
RUN apt-get update && apt-get install -y openjdk-17-jdk \
    && rm -rf /var/lib/apt/lists/*

# Install Maven 3.9.5
RUN wget https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz \
    && tar -xzf apache-maven-3.9.5-bin.tar.gz -C /opt/ \
    && ln -s /opt/apache-maven-3.9.5 /opt/maven \
    && rm apache-maven-3.9.5-bin.tar.gz

# Install Docker CLI (for Docker-in-Docker scenarios)
RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg \
    && echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null \
    && apt-get update && apt-get install -y docker-ce-cli \
    && rm -rf /var/lib/apt/lists/*

# Install Trivy
RUN wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | apt-key add - \
    && echo "deb https://aquasecurity.github.io/trivy-repo/deb $(lsb_release -sc) main" | tee -a /etc/apt/sources.list.d/trivy.list \
    && apt-get update && apt-get install -y trivy \
    && rm -rf /var/lib/apt/lists/*

# Install SonarQube Scanner
RUN wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-5.0.1.3006-linux.zip \
    && unzip sonar-scanner-cli-5.0.1.3006-linux.zip -d /opt/ \
    && ln -s /opt/sonar-scanner-5.0.1.3006-linux /opt/sonar-scanner \
    && rm sonar-scanner-cli-5.0.1.3006-linux.zip

# Install Hadolint (Dockerfile linter)
RUN wget https://github.com/hadolint/hadolint/releases/download/v2.12.0/hadolint-Linux-x86_64 \
    && chmod +x hadolint-Linux-x86_64 \
    && mv hadolint-Linux-x86_64 /usr/local/bin/hadolint

# Set environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV MAVEN_HOME=/opt/maven
ENV PATH=$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin:/opt/sonar-scanner/bin

# Create working directory
WORKDIR /workspace

# Create user for CI/CD operations
RUN groupadd docker || true && \
    useradd -m -s /bin/bash cicd && \
    usermod -aG docker cicd

# Switch to cicd user
USER cicd

CMD ["/bin/bash"]
