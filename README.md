[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0fc377c99d404e2bada322b98f4e6f52)](https://www.codacy.com/app/alfonsoserra/seed-jee?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=systelab/seed-jee&amp;utm_campaign=Badge_Grade)

# `seed-jee` — Seed for JEE Systelab projects

This project is an application skeleton for a typical [JEE][JEE] backend application. You can use it
to quickly bootstrap your projects and dev environment.

The seed contains a Patient Management sample application and is preconfigured to install the JEE
framework and a bunch of development and testing tools for instant development gratification.

The app doesn't do much, just shows how to use different JEE standards and other suggested tools together:

* Bean Validation.
* JAXB
* CDI
* JPA
* EJB
* JAX-RS
* JWT
* CORS
* Swagger
* Allure with JUnit

## Getting Started

To get you started you can simply clone the `seed-jee` repository and install the dependencies:

### Prerequisites

You need [git][git] to clone the `seed-jee` repository.

You will need [Java™ SE Development Kit 8][jdk-download] and [Maven][maven].

### Clone `seed-jee`

Clone the `seed-jee` repository using git:

```bash
git clone https://github.com/systelab/seed-jee.git
cd seed-jee
```

If you just want to start a new project without the `seed-jee` commit history then you can do:

```bash
git clone --depth=1 https://github.com/systelab/seed-jee.git <your-project-name>
```

The `depth=1` tells git to only pull down one commit worth of historical data.

### Install Dependencies

In order to install the dependencies you must run:

```bash
mvn install
```

To generate the reports including the test report, you must run:

```bash
mvn site
```

### Run

In order to run the application, you must install a WildFly and deploy the generated war file. 

Another option could be to change the pom.xml file and remove the jacoco from the <cargo.jvmargs>. 

```
<cargo.jvmargs>${cargo.container.debug.jvmargs}</cargo.jvmargs>
```

After the update type the following command:

```bash
mvn clean package cargo:run
```

Finally browse to: http://127.0.0.1:13080/seed/swagger/ (the port could changes as it is defined in the pom file).

Use 'quentinada' as username and password.

Note: If you are using the angular seed, remember to set the API_BASE_PATH in the environment to match the same port.

## Docker

### Build docker image

There is an Automated Build Task in Docker Cloud in order to build the Docker Image. 
This task, triggers a new build with every git push to your source code repository to create a 'latest' image.
There is another build rule to trigger a new tag and create a 'version-x.y.z' image

You can always manually create the image with the following command:

```bash
docker build -t systelab/seed-jee . 
```

The image created, will contain a [wildfly server][wildfly] with the application war deployed.

### Run the container

```bash
docker run -e MYSQL_HOST=ip -e MYSQL_PORT=port -e MYSQL_DATABASE=database -e MYSQL_USER=user -e MYSQL_PASSWORD=password -p 8080:8080 systelab/seed-jee
```
The app will be available at http://localhost:8080

In the repository folder, you will information on how to use Docker container, a tool for defining and running multi-container Docker applications. You will also find also a docker-compose.yml file with a configuration defined.


[git]: https://git-scm.com/
[maven]: https://maven.apache.org/download.cgi
[jdk-download]: http://www.oracle.com/technetwork/java/javase/downloads
[JEE]: http://www.oracle.com/technetwork/java/javaee/tech/index.html
[wildfly]: http://wildfly.org
