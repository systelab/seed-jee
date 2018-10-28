[![Build Status](https://travis-ci.org/systelab/seed-jee.svg?branch=master)](https://travis-ci.org/systelab/seed-jee)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0fc377c99d404e2bada322b98f4e6f52)](https://www.codacy.com/app/alfonsoserra/seed-jee?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=systelab/seed-jee&amp;utm_campaign=Badge_Grade)
[![Known Vulnerabilities](https://snyk.io/test/github/systelab/seed-jee/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/systelab/seed-jee?targetFile=pom.xml)

# `seed-jee` — Seed for JEE Systelab projects

This project is an application skeleton for a typical [JEE][JEE] backend application. You can use it
to quickly bootstrap your projects and dev environment.

The seed contains a Patient Management sample application and is preconfigured to install the JEE
framework and a bunch of development and testing tools for instant development gratification.

The app just shows how to use different JEE standards and other suggested tools together:

* [Bean Validation][beanvalidation].
* [JAXB][jaxb]
* [CDI][cdi]
* [JPA][jpa]
* [EJB][ejb]
* [JAX-RS][jaxrs]
* [JWT][jwt]
* [CORS][cors]
* [Swagger][swagger]
* [Allure][allure] with [JUnit][junit]
* [Lombok][lombok]

You can also check out our [Spring Boot implementation](https://github.com/systelab/seed-springboot).

## Getting Started

### Prerequisites

You need [git][git], [Java™ SE Development Kit 8][jdk-download] and [Maven][maven].

Ensure the environment variables are properly set: JAVA_HOME, MAVEN_HOME, M2_HOME and PATH.

### Clone `seed-jee`

Clone the `seed-jee` repository using git.

```bash
git clone https://github.com/systelab/seed-jee.git
cd seed-jee
```

> If you just want to start a new project without the `seed-jee` commit history then you can use the parameter "--depth=1" to only pull down one commit worth of historical data.

### Install Dependencies

In order to install the dependencies you must run:

```bash
mvn install
```
> In case of issues, refer to the Troubleshooting section

### Run with Cargo

Run the application with Cargo using the following command:

```bash
mvn clean package cargo:run
```

In order to check it's properly deployed, check the server at http://127.0.0.1:13080/seed/swagger/

Use 'Systelab' as username and password

Considerations:
> The port could changes as it is defined in the pom.xml file

> If you are using the angular seed, remember to set the API_BASE_PATH in the seed-angular environment to match the same port.

> As an alternative to use Cargo, you can also install a WildFly and MySQL database, and deploy the generated war file afterwards.

> To generate the reports including the Allure test report, you must run "mvn site". Once the reports have been generated, you can check them by browsing the folder target/site and opening the file allure-maven.html

> You need to install the Lombok library on your IDE such as eclipse.

## Automatic Test

Refer to the test package at /src/test

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

In the github root folder, you will find information on how to use docker-compose, a tool for define and run multi-container Docker applications.

## Kubernetes

In the github root folder, you will find information on how to run the solution in a [Kubernetes][kubernetes] cluster.

## Troubleshooting

### Maven in Windows

>If you are working in a Windows environment, you could have some issues if the maven local repository is in a folder with a name containing white spaces (quite common as the default value is ${user.home}/.m2/repository). In order to avoid this, it is fully recommended that you specify another folder in your maven settings.xml file.

For example:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <localRepository>/dev/repo</localRepository>

  ...
```

[git]: https://git-scm.com/
[maven]: https://maven.apache.org/download.cgi
[jdk-download]: http://www.oracle.com/technetwork/java/javase/downloads
[JEE]: http://www.oracle.com/technetwork/java/javaee/tech/index.html
[wildfly]: http://wildfly.org
[beanvalidation]:https://docs.oracle.com/javaee/7/tutorial/bean-validation001.htm
[jaxb]: https://docs.oracle.com/javaee/7/tutorial/jaxrs-advanced007.htm
[cdi]: https://docs.oracle.com/javaee/7/tutorial/cdi-basic.htm
[jpa]: https://docs.oracle.com/javaee/7/tutorial/jaxrs-advanced007.htm
[ejb]: https://docs.oracle.com/javaee/7/tutorial/partentbeans.htm#BNBLR
[jaxrs]: https://docs.oracle.com/javaee/7/tutorial/jaxrs.htm#GIEPU
[jwt]: https://jwt.io/
[cors]: https://en.wikipedia.org/wiki/Cross-origin_resource_sharing
[swagger]: https://swagger.io/
[allure]: https://docs.qameta.io/allure/
[junit]: https://junit.org/junit5/
[kubernetes]: https://kubernetes.io
[lombok]: https://projectlombok.org/

