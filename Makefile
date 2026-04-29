IMAGE=anskaffelser/validator

.DEFAULT_GOAL := help

.PHONY: help package test release docker_build docker_push docker_run rotate_keystore version

help: ## Show available targets
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

package: ## Build all modules (mvn clean package)
	@mvn -B --no-transfer-progress clean package

test: ## Build and run tests only (no Docker)
	@mvn -B --no-transfer-progress clean test

release: ## Prepare and perform Maven release (tags git, publishes to Maven repo)
	@mvn clean release:prepare release:perform

docker_build: ## Build multi-arch Docker image locally. Usage: make docker_build tag=dev
	@docker buildx build \
		--platform=linux/amd64,linux/arm64 \
		--progress plain \
		--tag $(IMAGE):$(tag) \
		.

docker_push: ## Build and push multi-arch Docker image to registry. Usage: make docker_push tag=edge
	@docker buildx build \
		--platform=linux/amd64,linux/arm64 \
		--progress plain \
		--tag $(IMAGE):$(tag) \
		--push \
		.

docker_run: ## Run validator container locally. Usage: make docker_run tag=edge
	@docker run --rm -it $(IMAGE):$(tag)

rotate_keystore: ## Rotate expired self-signed keystore in validator-build
	@bash validator-build/scripts/rotate-keystore.sh

version: ## Print current project version from pom.xml
	@mvn help:evaluate -Dexpression=project.version -q -DforceStdout
