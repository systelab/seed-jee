# Logging

Wildfly has its own logging subsystem.  The server logging configuration is defined in the standalone.xml (or standalone-full.xml) file.  
By default it includes two handlers; One for logging to the console and another for logging to a file (server.log).

WildFly has its own logging system but it also supports slf4j out-of-the-box.
SLF4J doesn’t provide logging capabilities. It is merely a facade (hence the name) for various logging frameworks, e.g. java.util.logging, log4j and logback, allowing the end user to plug in the desired logging framework at deployment time.
(URL: https://technology.first8.nl/configuring-slf4j-in-wildfly/)

There are 2 ways to customize slf4j&logback with Wildfly (read from http://loctranhoang.blogspot.com/2017/08/slf4j-with-logback-in-wildfly10.html) :
   - Stop implicit logging dependencies, enable slf4j&logback in ear or war application.
   - Still use Wildfly's implcit logging dependencies, but change default logging to slf4j with logback.

## 1.- Stop implicit logging dependencies, enable slf4j and logback in ear or war application

If you want to use a different binding you need to exclude the default logging module 
from being added to your deployment. In order to exclude the Wildfly logging, we have to create a 
jboss-deployment-structure.xml file (src/main/java/resources/META-INF):
(https://stackoverflow.com/questions/38435227/wildfly-disabling-logging-subsystem)

```xml
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

```xml
    <!-- it will pull in the logback-core and slf4j-api -->
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.2.3</version>
    </dependency>
```

### Configure Logback logs

The Logback configuration file (logback.xml) must be place in src/main/java/resources.

An example of logback.xml:

```xml
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

## 2. Change default Wildfly to sfl4j with logback.


 a. Edit  $JBOSS_HOME/standalone/configuration/standalone.xml, add system property using slf4j as logging provider.

```xml
<extensions>
...
</extensions>
<system-properties>
    <property name="org.jboss.logging.provider" value="slf4j"/>
</system-properties>
<management>
...
</management>
```

   b. Create module folder for logback, add libs and xml config file
    - create folder: $JBOSS_HOME/modules/system/layers/base/ch/qos/logback/main
    - dowload latest logback libs and put 2 jar files: logback-classic-1.2.3.jar, logback-core-1.2.3.jar in the previous created folder.
    - create a file named module.xml  with content
```xml
                <?xml version="1.0" encoding="UTF-8"?>

                 <module xmlns="urn:jboss:module:1.1" name="ch.qos.logback">
                     <resources>
                         <resource-root path="logback-classic-1.2.3.jar"/>
                        <resource-root path="logback-core-1.2.3.jar"/>
                     </resources>

                     <dependencies>
                         <module name="org.slf4j" />
                         <module name="javax.api" />
                         <module name="javax.mail.api" />
                     </dependencies>
                  </module>
```


 Note that jar file names and content of module.xml must match for the cases of updating jar files

 c. Change Jboss config to use slf4j and logback by opening file $JBOSS_HOME/modules/system/layers/base/org/jboss/logging/main/module.xml to:
 
```xml
<module xmlns="urn:jboss:module:1.3" name="org.jboss.logging">
    <resources>
        <resource-root path="jboss-logging-3.3.0.Final.jar"/>
    </resources>

    <dependencies>
        <!-- <module name="org.jboss.logmanager"/> -->
  <module name="org.slf4j"/>
  <module name="ch.qos.logback"/>
    </dependencies>
</module>
```
   d. configure sfl4j using logback implementation.
    - edit file: $JBOSS_HOME/modules/system/layers/base/org/slf4j/main/module.xml 
    change dependencies from 
```xml
        <dependencies>
  <!-- <module name="org.slf4j.impl"/> -->
  <module name="ch.qos.logback" />  
        </dependencies>
```
   e. add configuration file logback.xml to folder: $JBOSS_HOME/standalone/configuration
and change config standalone.sh (or standalone.bat) to load logback.xml

```bash
    standalone.sh (linux)
        export JAVA_OPTS="$JAVA_OPTS -DJBOSS_LOG_DIR=$JBOSS_HOME/standalone/log"

        export JAVA_OPTS="$JAVA_OPTS -        Dlogback.configurationFile=$JBOSS_HOME/standalone/configuration/logback.xml"

    standalone.bat (windows)
        set "JAVA_OPTS=%JAVA_OPTS% -DJBOSS_LOG_DIR=%JBOSS_HOME%\standalone\log "

        set "JAVA_OPTS=%JAVA_OPTS% -Dlogback.configurationFile=%JBOSS_HOME%\standalone\configuration\logback.xml"
```

## Errors while deploying WAR

error: **SLF4J: Class path contains multiple SLF4J bindings**


```
        11:35:49,105 ERROR [stderr] (ServerService Thread Pool -- 106) SLF4J: Found binding in [vfs:/C:/dev_software/app_servers/wildfly/wildfly-17.0.0.Final_SEED/content/seed.war/WEB-INF/lib/logback-classic-1.2.3.jar/org/slf4j/impl/StaticLoggerBinder.class]
        11:35:49,107 ERROR [stderr] (ServerService Thread Pool -- 106) SLF4J: Found binding in [vfs:/C:/dev_software/app_servers/wildfly/wildfly-17.0.0.Final_SEED/content/seed.war/WEB-INF/lib/slf4j-simple-1.7.26.jar/org/slf4j/impl/StaticLoggerBinder.class]
        11:35:49,108 ERROR [stderr] (ServerService Thread Pool -- 106) SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
        11:35:49,203 ERROR [stderr] (ServerService Thread Pool -- 106) SLF4J: Actual binding is of type [ch.qos.logback.classic.util.ContextSelectorStaticBinder]
```

Solution:  remove this dependency from pom.xml:

```xml
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-simple</artifactId>
			<version>1.7.26</version>
		</dependency>
```


---------------------------------------------------

How to print logs with SLF4J: Read SLF4J FAQ (http://www.slf4j.org/faq.html, chapter "About the SLF4J API")

URL's:  
 - Logback manual:  http://logback.qos.ch/manual/
 - SLF4J: http://www.slf4j.org/
 - Wildfly logging subsystem: https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/6.4/html/administration_and_configuration_guide/chap-the_logging_subsystem

