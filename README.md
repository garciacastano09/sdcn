
<--- DESPLIEGUE LOCAL EN ALTA DISPONIBILIDAD --->

docker network create --driver bridge zookeeper_net
docker run -e "ZOO_MY_ID=1" -e "ZOO_SERVERS=server.1=zk1:28881:38881 server.2=zk2:28882:38882 server.3=zk3:28883:38883" -p 21811:2181 -p 28881:2888 -p 38881:3888 --network=zookeeper_net --name zk1 --restart always -d zookeeper
docker run -e "ZOO_MY_ID=2" -e "ZOO_SERVERS=server.1=zk1:28881:38881 server.2=zk2:28882:38882 server.3=zk3:28883:38883" -p 21812:2181 -p 28882:2888 -p 38882:3888 --network=zookeeper_net --name zk2 --restart always -d zookeeper
docker run -e "ZOO_MY_ID=3" -e "ZOO_SERVERS=server.1=zk1:28881:38881 server.2=zk2:28882:38882 server.3=zk3:28883:38883" -p 21813:2181 -p 28883:2888 -p 38883:3888 --network=zookeeper_net --name zk3 --restart always -d zookeeper
docker run --name pg -e POSTGRES_USER=sdcn -e POSTGRES_PASSWORD=1234 -p 5432:5432 -d postgres


<--- DESTRUIR DESPLIEGUE LOCAL EN ALTA DISPONIBILIDAD --->

docker kill zk1
docker kill zk2
docker kill zk3
docker kill pg
docker rm zk1
docker rm zk2
docker rm zk3
docker rm pg


<--- DESPLIEGUE DISTRIBUIDO EN ALTA DISPONIBILIDAD (correr las lineas en maquinas diferentes sustituyendo las IPs)--->

docker run -e "ZOO_MY_ID=1" -e "ZOO_SERVERS=server.1=<zk1_IP_address>:2888:3888 server.2=<zk2_IP_address>:2888:3888 server.3=<zk3_IP_address>:2888:3888" -p 2888:2888 -p 3888:3888 --name zk1 --restart always -d zookeeper
docker run -e "ZOO_MY_ID=2" -e "ZOO_SERVERS=server.1=<zk1_IP_address>:2888:3888 server.2=<zk2_IP_address>:2888:3888 server.3=<zk3_IP_address>:2888:3888" -p 2888:2888 -p 3888:3888 --name zk2 --restart always -d zookeeper
docker run -e "ZOO_MY_ID=3" -e "ZOO_SERVERS=server.1=<zk1_IP_address>:2888:3888 server.2=<zk2_IP_address>:2888:3888 server.3=<zk3_IP_address>:2888:3888" -p 2888:2888 -p 3888:3888 --name zk3 --restart always -d zookeeper
docker run --name pg -e POSTGRES_USER=sdcn -e POSTGRES_PASSWORD=1234 -p 5432:5432 -d postgres


<--- UTILIZAR CLIENTE DE ZK PARA EXPLORAR ZK1 (cambiar nombre si se quiere explorar otro nodo) --->

docker exec -it zk1 sh -c "cd bin && ./zkCli.sh -server localhost:2181"



<--- UTILIZAR CLIENTE DE PG PARA EXPLORAR PG --->

docker run -it --rm --link pg:postgres postgres psql -h postgres -U sdcn
