package com.hendisantika.distributedcache.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A six-node Redis Cluster for integration tests, started once per JVM.
 * <p>
 * The ports are bound 1:1 rather than mapped to random host ports. Cluster nodes
 * gossip the address a client must reconnect to on a MOVED redirect, and they
 * announce {@code 127.0.0.1:<port>}; that address is only reachable from the test
 * JVM if the host port matches the container port. The range is deliberately
 * distinct from the compose stack's 7100-7105 so both can run side by side.
 */
public final class RedisClusterTestContainer {
    private static final int FIRST_PORT = 7200;
    private static final int NODE_COUNT = 6;
    private static final List<Integer> PORTS =
            IntStream.range(0, NODE_COUNT).mapToObj(i -> FIRST_PORT + i).toList();

    private static final GenericContainer<?> CONTAINER = create();

    private RedisClusterTestContainer() {
    }

    private static GenericContainer<?> create() {
        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
                .withCopyFileToContainer(
                        MountableFile.forHostPath("docker/start-redis-cluster.sh"),
                        "/docker/start-redis-cluster.sh")
                .withEnv("REDIS_CLUSTER_PORTS",
                        String.join(" ", PORTS.stream().map(String::valueOf).toList()))
                .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("/bin/sh", "/docker/start-redis-cluster.sh"))
                .waitingFor(Wait.forLogMessage(".*Redis Cluster ready\\..*", 1)
                        .withStartupTimeout(Duration.ofMinutes(3)));
        container.setPortBindings(PORTS.stream().map(port -> port + ":" + port).toList());
        return container;
    }

    /** Starts the shared cluster if needed and points the app's node list at it. */
    public static void registerNodes(DynamicPropertyRegistry registry) {
        if (!CONTAINER.isRunning()) {
            CONTAINER.start();
        }
        for (int i = 0; i < NODE_COUNT; i++) {
            String node = "127.0.0.1:" + PORTS.get(i);
            registry.add("cache.redis.config.nodes[" + i + "]", () -> node);
        }
    }
}
