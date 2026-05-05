# Base image docs: https://github.com/devcontainers/images/tree/main/src/base
FROM mcr.microsoft.com/devcontainers/base:ubuntu-24.04

# Core tools needed for SDKMAN/NVM and Java installs
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends \
       curl ca-certificates zip unzip bash git \
    && rm -rf /var/lib/apt/lists/*

# Install SDKMAN for the vscode user
RUN su vscode -c "curl -s https://get.sdkman.io | bash"

# Install Java 21 (Temurin) and make it default
RUN su vscode -c "source /usr/local/sdkman/bin/sdkman-init.sh \
    && sdk install java 21-tem \
    && sdk default java 21-tem"

# Install NVM for the vscode user
RUN su vscode -c "curl -fsSL https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.3/install.sh | bash"

# [Option] Install Maven
ARG INSTALL_MAVEN="true"
ARG MAVEN_VERSION="3.9.15"

# [Option] Install Gradle
ARG INSTALL_GRADLE="false"
ARG GRADLE_VERSION=""

RUN if [ "${INSTALL_MAVEN}" = "true" ]; then \
      su vscode -c "source /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven \"${MAVEN_VERSION}\""; \
    fi \
    && if [ "${INSTALL_GRADLE}" = "true" ]; then \
      su vscode -c "source /usr/local/sdkman/bin/sdkman-init.sh && sdk install gradle \"${GRADLE_VERSION}\""; \
    fi

# [Option] Install Node.js
ARG INSTALL_NODE="true"
ARG NODE_VERSION="lts/*"

RUN if [ "${INSTALL_NODE}" = "true" ]; then \
      su vscode -c "source /usr/local/share/nvm/nvm.sh && nvm install ${NODE_VERSION}"; \
    fi

RUN source /home/vscode/.sdkman/bin/sdkman-init.sh
RUN source /home/vscode/.nvm/nvm.sh