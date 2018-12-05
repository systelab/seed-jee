# Docker

To create the container, execute the command:

```bash
docker run -e MYSQL_HOST=ip -e MYSQL_PORT=port -e MYSQL_DATABASE=database -e MYSQL_USER=user -e MYSQL_PASSWORD=password -p 8443:8443 -p 8080:8080 systelab/seed-jee
```

Usually the mySQL database port is 3306.

You can create both containers with a docker-compose.yml file and the command docker-compose.

In the repository you will find a basic docker-compose.yml file. In order to run the configuration use the command:

```bash
docker-compose up -d
```

Once started, browse https://localhost:8443/seed/swagger/ or http://localhost:8080/seed/swagger/ in order to get the API main page. To see your container CPU utilization, memory, etc. visit the cAdvisor URL: http://localhost:9090

There is also a docker-compose-with-elk.yml file. In this case, several containers will start, with traefik as a reverse proxy, mysql, two backend servers, seed-angular as frontend in a nginx server and elasticsearch, logstash and kibana. In order to run this configuration use the command:

```bash
docker-compose -f docker-compose-with-elk.yml up -d
```

Browse http://localhost to run the application, http://localhost:90/seed/swagger/ in order to get the API main page, http://localhost:8080/ to get traefik  and http://localhost:5601/ to get the Kibana main page. To see the container CPU utilization, memory, etc. visit the cAdvisor URL: http://localhost:9090.

> [Diagramr](http://diagramr.inventage.com/) could help you in order to understand the docker-compose files.
