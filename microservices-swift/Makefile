# BUILD FOR DEPLOYMENT
create_docker_images: create_build_image create_run_image

build_on_docker: create_build_image
	docker run --volume $$PWD:/swift-project --workdir /swift-project myapp-build /swift-utils/tools-utils.sh build release
run_on_docker: build_on_docker create_run_image
	docker run -it --publish 8080:8080 myapp-run
create_build_image:
	docker build --tag myapp-build --file Dockerfile-tools .
create_run_image:
	docker build --tag myapp-run .