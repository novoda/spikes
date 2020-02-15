## Scaffolded Swift Kitura server application

This scaffolded application provides a starting point for creating Swift applications running on [Kitura](http://www.kitura.io/).

### Table of Contents
* [Requirements](#requirements)
* [Project contents](#project-contents)
* [Run](#run)
* [Configuration](#configuration)
* [Deploy to IBM Cloud](#deploy-to-ibm-cloud)
* [Service descriptions](#service-descriptions)
* [License](#license)
* [Generator](#generator)

#### Project contents
This application has been generated with the following capabilities and services, which are described in full in their respective sections below:

* [CloudEnvironment](#configuration)
* [Embedded metrics dashboard](#embedded-metrics-dashboard)
* [Docker files](#docker-files)
* [Iterative Development](#iterative-development)
* [IBM Cloud deployment](#ibm-cloud-deployment)


### Requirements
* [Swift 4](https://swift.org/download/)

### Run
To build and run the application:
1. `swift build`
1. `.build/debug/microservices-swift`

#### Docker
A description of the files related to Docker can be found in the [Docker files](#docker-files) section. To build the two Docker images, run the following commands from the root directory of the project.

First, build the 'tools' image. This image contains a full Linux Swift toolchain and is capable of compiling your application for Linux:
* `docker build --tag myapp-build --file Dockerfile-tools .`
You may customize the names of these images by specifying a different value after the `-t` option.

To compile the application for Linux using the tools Docker image, run:
* `docker run --volume $PWD:/swift-project --workdir /swift-project myapp-build /swift-utils/tools-utils.sh build release`
This produces a `.build-ubuntu` directory which will be incorporated into your final image.

Now that your application has been built, you can build the final run image:
* `docker build --tag myapp-run .`

Finally, to run the application:
* `docker run -it --publish 8080:8080 myapp-run`


#### Kubernetes
To deploy your application to your Kubernetes cluster, run `helm install --name myapp .` in the `/chart/microservices-swift` directory. You need to make sure you change the `repository` variable in your `chart/microservices-swift/values.yaml` file points to the docker image containing your runnable application.

### Configuration
Your application configuration information for any services is stored in the `localdev-config.json` file in the `config` directory. This file is in the `.gitignore` to prevent sensitive information from being stored in git. The connection information for any configured services that you would like to access when running locally, such as username, password and hostname, is stored in this file.

The application uses the [CloudEnvironment package](https://github.com/IBM-Swift/CloudEnvironment) to read the connection and configuration information from the environment and this file. It uses `mappings.json`, found in the `config` directory, to communicate where the credentials can be found for each service.

If the application is running locally, it can connect to IBM Cloud services using unbound credentials read from this file. If you need to create unbound credentials you can do so from the IBM Cloud web console ([example](https://cloud.ibm.com/docs/services/Cloudant/tutorials/create_service.html#creating-a-service-instance)), or using the CloudFoundry CLI [`cf create-service-key` command](http://cli.cloudfoundry.org/en-US/cf/create-service-key.html).

When you push your application to IBM Cloud, these values are no longer used, instead the application automatically connects to bound services using environment variables.

#### Iterative Development
The `iterative-dev.sh` script is included in the root of the generated Swift project and allows for fast & easy iterations for the developer. Instead of stopping the running Kitura server to see new code changes, while the script is running, it will automatically detect changes in the project's **.swift** files and recompile the app accordingly.

To use iterative development:
* For native OS, execute the `./iterative-dev.sh` script from the root of the project.
* With docker, shell into the tools container mentioned above, and run the `./swift-project/iterative-dev.sh` script.  File system changes are detected using a low-tech infinitely looping poll mechanism, which works in both local OS/filesystem and across host OS->Docker container volume scenarios.

### Deploy to IBM Cloud
You can deploy your application to IBM Cloud using:
* the [CloudFoundry CLI](#cloudfoundry-cli)
* an [IBM Cloud toolchain](#ibm-cloud-toolchain)

#### CloudFoundry CLI
You can deploy the application using the IBM Cloud command-line:
1. Install the [IBM Cloud CLI](https://cloud.ibm.com/docs/cli/index.html)
1. Ensure all configured services have been provisioned
1. Run `ibmcloud app push` from the project root directory

The Cloud Foundry CLI will not provision the configured services for you, so you will need to do this manually using the IBM Cloud web console ([example](https://cloud.ibm.com/docs/services/Cloudant/tutorials/create_service.html#creating-a-service-instance)) or the CloudFoundry CLI (`cf create-service` command)[http://cli.cloudfoundry.org/en-US/cf/create-service.html]. The service names and types will need to match your [configuration](#configuration).

#### IBM Cloud toolchain
You can also set up a default IBM Cloud Toolchain to handle deploying your application to IBM Cloud. This is achieved by publishing your application to a publicly accessible github repository and using the "Create Toolchain" button below. In this case configured services will be automatically provisioned, once, during toolchain creation.

[![Create Toolchain](https://cloud.ibm.com/devops/graphics/create_toolchain_button.png)](https://cloud.ibm.com/devops/setup/deploy/)

### Service descriptions
#### Embedded metrics dashboard
This application uses the [SwiftMetrics package](https://github.com/RuntimeTools/SwiftMetrics) to gather application and system metrics.

These metrics can be viewed in an embedded dashboard on `/swiftmetrics-dash`. The dashboard displays various system and application metrics, including CPU, memory usage, HTTP response metrics and more.
#### Docker files
The application includes the following files for Docker support:
* `.dockerignore`
* `Dockerfile`
* `Dockerfile-tools`

The `.dockerignore` file contains the files/directories that should not be included in the built docker image. By default this file contains the `Dockerfile` and `Dockerfile-tools`. It can be modified as required.

The `Dockerfile` defines the specification of the default docker image for running the application. This image can be used to run the application.

The `Dockerfile-tools` is a docker specification file similar to the `Dockerfile`, except it includes the tools required for compiling the application. This image can be used to compile the application.

Details on how to build the docker images, compile and run the application within the docker image can be found in the [Run section](#run).
#### IBM Cloud deployment
Your application has a set of cloud deployment configuration files defined to support deploying your application to IBM Cloud:
* `manifest.yml`
* `.bluemix/toolchain.yml`
* `.bluemix/pipeline.yml`

The [`manifest.yml`](https://cloud.ibm.com/docs/cloud-foundry/deploy-apps.html#appmanifest) defines options which are passed to the Cloud Foundry `cf push` command during application deployment.

[IBM Cloud DevOps](https://cloud.ibm.com/docs/services/ContinuousDelivery/index.html#cd_getting_started) service provides toolchains as a set of tool integrations that support development, deployment, and operations tasks inside IBM Cloud, for both Cloud Foundry and Kubernetes applications. The ["Create Toolchain"](#deploy-to-ibm-cloud) button creates a DevOps toolchain and acts as a single-click deploy to IBM Cloud including provisioning all required services.


### License
All generated content is available for use and modification under the permissive MIT License (see `LICENSE` file), with the exception of SwaggerUI which is licensed under an Apache-2.0 license (see `NOTICES.txt` file).

### Generator
This project was generated with [generator-swiftserver](https://github.com/IBM-Swift/generator-swiftserver) v5.15.0.
