# spring-boot-distributed-cache

A Spring Boot 4 service that caches items in a **Redis Cluster**, storing each value
compressed (Snappy or Gzip) behind a configurable TTL.

## Requirements

- Java 21
- Docker (for the Redis Cluster, and for the integration tests)

## Running

`spring-boot-docker-compose` brings the cluster up automatically, so a single command
is enough:

```bash
./mvnw spring-boot:run
```

That starts `compose.yml`, which runs a six-node Redis Cluster (3 masters + 3
replicas) on ports **7100-7105**, then starts the app on <http://localhost:8080>.

To manage the cluster by hand instead:

```bash
docker compose up -d      # start
docker compose down       # stop, keeping the cluster's data volume
docker compose down -v    # stop and wipe the cluster
```

## API

```bash
# Cache an item
curl -X POST http://localhost:8080/api/v1/items \
  -H 'Content-Type: application/json' \
  -d '{"id":42,"name":"Kopi Gayo","price":19.99}'

# Read it back
curl http://localhost:8080/api/v1/items/42
# {"id":42,"name":"Kopi Gayo","price":19.99}

# Anything not cached falls back
curl http://localhost:8080/api/v1/items/9999
# {"msg":"Cache miss;"}
```

Swagger UI is at <http://localhost:8080/swagger-ui.html>, the OpenAPI document at
<http://localhost:8080/v3/api-docs>.

## Configuration

Everything lives under `cache.redis` in `src/main/resources/application.yml`:

| Property                              | Default          | Meaning                                     |
|---------------------------------------|------------------|---------------------------------------------|
| `cache.redis.ttl`                     | `1m`             | How long a cached item survives             |
| `cache.redis.compression-algorithm`   | `SNAPPY`         | `SNAPPY` or `GZIP`                          |
| `cache.redis.config.nodes`            | `localhost:7100` … `:7105` | Cluster seed nodes, as `host:port` |
| `cache.redis.config.read-timeout`     | `1s`             | Lettuce command timeout                     |
| `cache.redis.config.max-total-pool`   | `8`              | Connection pool size                        |

## Tests

```bash
./mvnw test      # unit tests only — fast, no Docker needed
./mvnw verify    # adds the *IT integration tests against a real cluster
```

The integration tests start their own six-node cluster on ports **7200-7205** via
Testcontainers, so they do not collide with the compose stack.

## How the cluster is wired

Redis Cluster hands clients a redirect address on `MOVED`, so every node must
advertise an address the client can actually dial. All six nodes therefore run in one
container (`docker/start-redis-cluster.sh`) announcing `127.0.0.1:<port>`, and each
port is published 1:1 — the same address then resolves correctly both for gossip
between nodes and for a client on the Docker host.

Ports 7100-7105 are used rather than the more common 7000-7005 because macOS binds
port 7000 to the AirPlay Receiver.
