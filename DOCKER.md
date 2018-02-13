# Docker

To create the container, execute the command:

```bash
docker run -it -e MYSQL_HOST=ip -e MYSQL_PORT=port -e MYSQL_DATABASE=database -e MYSQL_USER=user -e MYSQL_PASSWORD=password -p 8080:8080 seed-jee
```

Usually the mySQL database port is 3306

You can create both containers with a docker-compose.yml file and the command docker-compose up -d

Here's a sample docker-compose.yml file:

```yaml
version: "2"
services:
  db:
    image: mysql:latest
    ports:
      - "3307:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=supersecret
      - MYSQL_DATABASE=SEED
      - MYSQL_USER=SEED
      - MYSQL_PASSWORD=SEED
  web:
    image: systelab/seed-jee:latest
    depends_on:
      - db
    environment:
      - MYSQL_URI=db:3306
      - MYSQL_HOST=db
      - MYSQL_PORT=3306
      - MYSQL_DATABASE=SEED
      - MYSQL_USER=SEED
      - MYSQL_PASSWORD=SEED
    ports:
      - "8080:8080"
```

Here's another docker-compose.yml file, but in that case with traefik as a reverse proxy.

```yaml
version: "2"
services:
  traefik:
    image: traefik
    command: --web --docker --docker.domain=docker.localhost --logLevel=DEBUG
    ports:
      - "80:80"
      - "8080:8080"
      - "443:443"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - /dev/null:/traefik.toml
  db:
    image: mysql:latest
    ports:
      - "3307:3306"
    environment:
      - MYSQL_ROOT_PASSWORD= supersecret
      - MYSQL_DATABASE=SEED
      - MYSQL_USER=SEED
      - MYSQL_PASSWORD=SEED
  web:
    image: systelab/seed-jee:latest
    labels:
      - "traefik.frontend.rule=PathPrefix:/"
      - "traefik.port=8080"
    depends_on:
      - db
    environment:
      - MYSQL_URI=db:3306
      - MYSQL_HOST=db
      - MYSQL_PORT=3306
      - MYSQL_DATABASE=SEED
      - MYSQL_USER=SEED
      - MYSQL_PASSWORD=SEED
```
