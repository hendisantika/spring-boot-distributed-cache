#!/bin/sh
# Boots a 6-node Redis Cluster (3 masters + 3 replicas) inside a single container.
#
# All nodes live in one container on purpose: cluster nodes gossip the address a
# client should reconnect to, so every node announces 127.0.0.1:<port>. That
# address is valid both for the sibling nodes (same network namespace) and for a
# client on the Docker host, because each port is published 1:1.
set -eu

PORTS="7100 7101 7102 7103 7104 7105"
REPLICAS=1

for port in ${PORTS}; do
  mkdir -p "/data/${port}"
  redis-server \
    --port "${port}" \
    --dir "/data/${port}" \
    --cluster-enabled yes \
    --cluster-config-file nodes.conf \
    --cluster-node-timeout 5000 \
    --cluster-announce-ip 127.0.0.1 \
    --cluster-announce-port "${port}" \
    --cluster-announce-bus-port "1${port}" \
    --appendonly yes \
    --daemonize no &
done

for port in ${PORTS}; do
  while ! redis-cli -p "${port}" ping 2>/dev/null | grep -q PONG; do
    sleep 0.2
  done
done

first_port=$(echo "${PORTS}" | cut -d' ' -f1)

# A node that has never joined a cluster reports exactly one known node. Using
# that instead of cluster_state avoids a restart race: after a restart the nodes
# rediscover each other asynchronously, so cluster_state is briefly "fail" even
# though the cluster is already formed, and re-running create would abort.
known_nodes=$(redis-cli -p "${first_port}" cluster info | awk -F: '/cluster_known_nodes/ {print $2 + 0}')

if [ "${known_nodes}" -gt 1 ]; then
  echo "Redis Cluster already formed (${known_nodes} known nodes), waiting for it to settle."
  while ! redis-cli -p "${first_port}" cluster info | grep -q 'cluster_state:ok'; do
    sleep 0.5
  done
else
  echo "Forming Redis Cluster over ports: ${PORTS}"
  # shellcheck disable=SC2086 # word splitting builds the node list
  redis-cli --cluster create $(for p in ${PORTS}; do printf '127.0.0.1:%s ' "${p}"; done) \
    --cluster-replicas "${REPLICAS}" --cluster-yes
fi

echo "Redis Cluster ready."
wait
