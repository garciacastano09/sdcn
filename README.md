## BUILD DEL SERVIDOR
mvn clean prepare-package war:exploded
docker build . --tag=sdcn_server

## DESPLIEGUE LOCAL EN ALTA DISPONIBILIDAD

# Creacion de redes
docker network create --driver bridge sdcn_net

# Levantar Zookeeper
docker run -e "ZOO_MY_ID=1" -e "ZOO_SERVERS=server.1=zk1:28881:38881 server.2=zk2:28882:38882 server.3=zk3:28883:38883" -p 21811:2181 -p 28881:2888 -p 38881:3888 --network=sdcn_net --name zk1 --restart always -d zookeeper
docker run -e "ZOO_MY_ID=2" -e "ZOO_SERVERS=server.1=zk1:28881:38881 server.2=zk2:28882:38882 server.3=zk3:28883:38883" -p 21812:2181 -p 28882:2888 -p 38882:3888 --network=sdcn_net --name zk2 --restart always -d zookeeper
docker run -e "ZOO_MY_ID=3" -e "ZOO_SERVERS=server.1=zk1:28881:38881 server.2=zk2:28882:38882 server.3=zk3:28883:38883" -p 21813:2181 -p 28883:2888 -p 38883:3888 --network=sdcn_net --name zk3 --restart always -d zookeeper

# Levantar Servidores
docker run --name server1 -e DATABASE_ADDRESS=pg1 -p 8081:8080 --network=sdcn_net sdcn_server
docker run --name server2 -e DATABASE_ADDRESS=pg2 -p 8082:8080 --network=sdcn_net sdcn_server
docker run --name server3 -e DATABASE_ADDRESS=pg3 -p 8083:8080 --network=sdcn_net sdcn_server
docker run --name server4 -e DATABASE_ADDRESS=pg4 -p 8084:8080 --network=sdcn_net sdcn_server

# Levantar Postgres
docker run --name pg1 -e POSTGRES_USER=sdcn -e POSTGRES_PASSWORD=1234 -p 54321:5432 --network=sdcn_net -d postgres
docker run --name pg2 -e POSTGRES_USER=sdcn -e POSTGRES_PASSWORD=1234 -p 54322:5432 --network=sdcn_net -d postgres
docker run --name pg3 -e POSTGRES_USER=sdcn -e POSTGRES_PASSWORD=1234 -p 54323:5432 --network=sdcn_net -d postgres
docker run --name pg4 -e POSTGRES_USER=sdcn -e POSTGRES_PASSWORD=1234 -p 54324:5432 --network=sdcn_net -d postgres


## DESTRUIR DESPLIEGUE LOCAL EN ALTA DISPONIBILIDAD

docker kill zk1
docker kill zk2
docker kill zk3
docker kill pg1
docker kill pg2
docker kill pg3
docker kill pg4
docker kill pg4
docker kill server1
docker kill server2
docker kill server3
docker kill server4
docker rm zk1
docker rm zk2
docker rm zk3
docker rm pg1
docker rm pg2
docker rm pg3
docker rm pg4
docker rm pg4
docker rm server1
docker rm server2
docker rm server3
docker rm server4

## UTILIZAR CLIENTE DE ZK PARA EXPLORAR ZK1 (cambiar nombre si se quiere explorar otro nodo)

docker exec -it zk1 sh -c "cd bin && ./zkCli.sh -server localhost:2181"


## UTILIZAR CLIENTE DE PG PARA EXPLORAR PG

docker run -it --rm --link pg:postgres postgres psql -h postgres -U sdcn
