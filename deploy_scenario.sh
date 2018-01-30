#!/usr/bin/env bash

# Construccion servidor
mvn clean package
docker build . --tag=sdcn_server

# Creacion de redes
docker network create --driver bridge sdcn_net

# Levantar Zookeeper
docker run --name zk1 -e "ZOO_MY_ID=1" -e "ZOO_SERVERS=server.1=zk1:28881:38881 server.2=zk2:28882:38882 server.3=zk3:28883:38883" -p 21811:2181 -p 28881:2888 -p 38881:3888 --network=sdcn_net --restart always -d zookeeper
docker run --name zk2 -e "ZOO_MY_ID=2" -e "ZOO_SERVERS=server.1=zk1:28881:38881 server.2=zk2:28882:38882 server.3=zk3:28883:38883" -p 21812:2181 -p 28882:2888 -p 38882:3888 --network=sdcn_net --restart always -d zookeeper
docker run --name zk3 -e "ZOO_MY_ID=3" -e "ZOO_SERVERS=server.1=zk1:28881:38881 server.2=zk2:28882:38882 server.3=zk3:28883:38883" -p 21813:2181 -p 28883:2888 -p 38883:3888 --network=sdcn_net --restart always -d zookeeper

# Levantar Postgres
docker run --name pg1 -e POSTGRES_USER=sdcn -e POSTGRES_PASSWORD=1234 -p 54321:5432 --network=sdcn_net -d postgres
docker run --name pg2 -e POSTGRES_USER=sdcn -e POSTGRES_PASSWORD=1234 -p 54322:5432 --network=sdcn_net -d postgres
docker run --name pg3 -e POSTGRES_USER=sdcn -e POSTGRES_PASSWORD=1234 -p 54323:5432 --network=sdcn_net -d postgres
docker run --name pg4 -e POSTGRES_USER=sdcn -e POSTGRES_PASSWORD=1234 -p 54324:5432 --network=sdcn_net -d postgres

# Levantar Servidores
#docker run --name server1 -e DATABASE_ADDRESS=pg1 -p 8081:8080 --network=sdcn_net sdcn_server
#docker run --name server2 -e DATABASE_ADDRESS=pg2 -p 8082:8080 --network=sdcn_net sdcn_server
#docker run --name server3 -e DATABASE_ADDRESS=pg3 -p 8083:8080 --network=sdcn_net sdcn_server
#docker run --name server4 -e DATABASE_ADDRESS=pg4 -p 8084:8080 --network=sdcn_net sdcn_server