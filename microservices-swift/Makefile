# CONSTANTS
BUILD_IMAGE_TAG=boardgame-build
RUN_IMAGE_TAG=boardgame-run
DB_IMAGE_TAG=boardgame-db
DB_PATH=db
DB_PORT=5984

# UTILITY
xcode:
	swift package generate-xcodeproj

# BUILD FOR DEPLOYMENT
run_on_docker: build_on_docker create_run_image create_db_image
	docker-compose up
build_on_docker: create_build_image
	docker run --volume $$PWD:/swift-project --workdir /swift-project $(BUILD_IMAGE_TAG) /swift-utils/tools-utils.sh build release
create_build_image:
	docker build --tag $(BUILD_IMAGE_TAG) --file Dockerfile-tools .
create_run_image:
	docker build --tag $(RUN_IMAGE_TAG) .
create_db_image:
	docker build --tag $(DB_IMAGE_TAG) --file Dockerfile-db .
run_db_image:
	docker run --volume $$PWD/$(DB_PATH):/opt/couchdb/data -p $(DB_PORT):$(DB_PORT) $(DB_IMAGE_TAG)