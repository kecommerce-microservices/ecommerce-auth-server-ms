#/bin/bash

PROFILES=$1

# Creating folders with permissions
echo "Creating folders with permissions"

mkdir -m 777 .docker

# Creating docker networks
echo "Creating docker networks"

docker network create ecommerce-auth-server-ms-network
docker network create ecommerce-network

# Running docker-compose
echo "Running docker-compose"

COMPOSE_PROFILES=$PROFILES docker-compose -f app/docker-compose.yml up -d

echo "Initializing containers"
sleep 20