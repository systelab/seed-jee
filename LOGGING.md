# Logging

Wildfly has its own logging subsystem.  The server logging configuration is defined in the standalone.xml (or standalone-full.xml) file.  
By default it includes two handlers; One for logging to the console and another for logging to a file (server.log).

WildFly has its own logging system but it also supports slf4j out-of-the-box.
SLF4J doesn’t provide logging capabilities. It is merely a facade (hence the name) for various logging frameworks, e.g. java.util.logging, log4j and logback, allowing the end user to plug in the desired logging framework at deployment time.
(URL: https://technology.first8.nl/configuring-slf4j-in-wildfly/)

If you want to use a different binding you need to exclude the default logging module 
from being added to your deployment. In order to exclude the Wildfly logging, we have to create a 
jboss-deployment-structure.xml file (src/main/java/resources/META-INF):
(https://stackoverflow.com/questions/38435227/wildfly-disabling-logging-subsystem)
```
    <?xml version="1.0" encoding="UTF-8"?>
    <jboss-deployment-structure>
        <deployment>
            <exclude-subsystems>
                <subsystem name="logging" />
            </exclude-subsystems>
            <dependencies>
                <module name="org.dom4j"/>
            </dependencies>
        </deployment>
    </jboss-deployment-structure>
```
In pom.xml of our application, we need to add a dependency to slfj4+logback in pom.xml:
```
    <!-- it will pull in the logback-core and slf4j-api -->
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.2.3</version>
    </dependency>
```

##Configure Logback logs

The Logback configuration file (logback.xml) must be place in src/main/java/resources.

An example of logback.xml:
```
<configuration>
  <appender name="FILE" class="ch.qos.logback.core.FileAppender">
    <file>./standalone/log/seed_slf4j_logback.log</file>
    <append>true</append>
    <encoder>
      <Pattern>%d{HH:mm} [%thread] %-5level %logger{52} - %msg%n</Pattern>
    </encoder>
  </appender>
  <root>
    <level value="TRACE"/>
    <appender-ref ref="FILE"/>
  </root>
</configuration>
```

How to print logs with SLF4J: Read SLF4J FAQ (http://www.slf4j.org/faq.html, chapter "About the SLF4J API")

URL's:  
 - Logback manual:  http://logback.qos.ch/manual/
 - SLF4J: http://www.slf4j.org/
 - Wildfly logging subsystem: https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/6.4/html/administration_and_configuration_guide/chap-the_logging_subsystem

