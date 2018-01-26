#!/usr/bin/env bash

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
docker network rm sdcn_net
