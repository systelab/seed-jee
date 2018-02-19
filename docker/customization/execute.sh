#!/bin/bash

# Usage: execute.sh [WildFly mode] [configuration file]
#
# The default mode is 'standalone' and default configuration is based on the
# mode. It can be 'standalone.xml' or 'domain.xml'.

JBOSS_HOME=/opt/jboss/wildfly
JBOSS_CLI=$JBOSS_HOME/bin/jboss-cli.sh
JBOSS_MODE=${1:-"standalone"}
JBOSS_CONFIG=${2:-"$JBOSS_MODE.xml"}

function wait_for_server() {
  until `$JBOSS_CLI -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do
    sleep 1
  done
}



echo "=> Starting WildFly server"
$JBOSS_HOME/bin/$JBOSS_MODE.sh -b 0.0.0.0 -c $JBOSS_CONFIG &

echo "=> Waiting for the server to boot"
wait_for_server

echo "=> Executing the commands"
echo "=> MYSQL_HOST (mySQL server): " $MYSQL_HOST
echo "=> MYSQL_PORT (mySQL port): " $MYSQL_PORT
echo "=> MYSQL_URI (mySQL URI): " $MYSQL_URI
echo "=> LOGSTASH_HOST (logstash server): " $LOGSTASH_HOST
echo "=> LOGSTASH_PORT (logstash port): " $LOGSTASH_PORT


# Wait for the DB Server
/opt/jboss/wildfly/customization/wait-for-it.sh $MYSQL_HOST:$MYSQL_PORT -t 0

$JBOSS_CLI -c << EOF
batch

# Add MySQL module
module add --name=com.mysql --resources=/opt/jboss/wildfly/customization/mysql-connector-java-5.1.45-bin.jar --dependencies=javax.api,javax.transaction.api

# Add MySQL driver
/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql,driver-xa-datasource-class-name=com.mysql.jdbc.jdbc2.optional.MysqlXADataSource)

# Add the datasource
data-source add --name=SEED --driver-name=mysql --jndi-name=java:/SEED --connection-url=jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$MYSQL_DATABASE?useUnicode=true&amp;characterEncoding=UTF-8 --user-name=$MYSQL_USER --password=$MYSQL_PASSWORD --use-ccm=false --max-pool-size=25 --blocking-timeout-wait-millis=5000 --enabled=true

# Execute the batch
run-batch

EOF

# Setup Logstash
if [ -z ${LOGSTASH_HOST+x} ]; then
    echo "LOGSTASH_HOST is unset. No need to wait";
else

/opt/jboss/wildfly/customization/wait-for-it.sh $LOGSTASH_HOST:$LOGSTASH_PORT -t 0

$JBOSS_CLI -c << EOF
batch

# Add the module, replace the directory on the resources attribute to the path where you downloaded the jboss-logmanager-ext library
module add --name=org.jboss.logmanager.ext --dependencies=org.jboss.logmanager,javax.json.api,javax.xml.stream.api --resources=/opt/jboss/wildfly/customization/jboss-logmanager-ext-1.0.0.Alpha3.jar

# Add the logstash formatter
/subsystem=logging/custom-formatter=logstash:add(class=org.jboss.logmanager.ext.formatters.LogstashFormatter,module=org.jboss.logmanager.ext)

# Add a socket-handler using the logstash formatter. Replace the hostname and port to the values needed for your logstash install
/subsystem=logging/custom-handler=logstash-handler:add(class=org.jboss.logmanager.ext.handlers.SocketHandler,module=org.jboss.logmanager.ext,named-formatter=logstash,properties={hostname=$LOGSTASH_HOST, port=$LOGSTASH_PORT})

# Add the new handler to the root-logger
/subsystem=logging/root-logger=ROOT:add-handler(name=logstash-handler)

# Execute the batch
run-batch

EOF

fi



# Deploy the WAR
cp /opt/jboss/wildfly/customization/seed.war $JBOSS_HOME/$JBOSS_MODE/deployments/seed.war

echo "=> Shutting down WildFly"
if [ "$JBOSS_MODE" = "standalone" ]; then
  $JBOSS_CLI -c ":shutdown"
else
  $JBOSS_CLI -c "/host=*:shutdown"
fi

echo "=> Restarting WildFly"
$JBOSS_HOME/bin/$JBOSS_MODE.sh -b 0.0.0.0 -c $JBOSS_CONFIG

