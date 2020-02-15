# CONSTANTS
PORT=8080
BUILD_IMAGE_TAG=boardgame-build
RUN_IMAGE_TAG=boardgame-run

# UTILITY
xcode:
	swift package generate-xcodeproj
# BUILD FOR DEPLOYMENT
run_on_docker: build_on_docker create_run_image
	docker run -it --publish $(PORT):$(PORT) $(RUN_IMAGE_TAG)
build_on_docker: create_build_image
	docker run --volume $$PWD:/swift-project --workdir /swift-project $(BUILD_IMAGE_TAG) /swift-utils/tools-utils.sh build release
create_build_image:
	docker build --tag $(BUILD_IMAGE_TAG) --file Dockerfile-tools .
create_run_image:
	docker build --tag $(RUN_IMAGE_TAG) .
