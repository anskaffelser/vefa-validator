IMAGE=anskaffelser/validator

package:
	@mvn clean package

release:
	@mvn clean release:prepare release:perform

docker_build:
	@DOCKER_CLI_EXPERIMENTAL=enabled docker buildx build \
		--platform=linux/amd64,linux/arm64 \
		--progress plain \
		--tag $(IMAGE):$(tag) \
		.

docker_push:
	@DOCKER_CLI_EXPERIMENTAL=enabled docker buildx build \
		--platform=linux/amd64,linux/arm64 \
		--progress plain \
		--tag $(IMAGE):$(tag) \
		--push \
		.