#!/bin/bash

docker compose down --volumes
docker stop $(docker ps -aq)
docker rm $(docker ps -aq)
docker rmi $(docker images -q)

cd ./app

mvn clean
mvn compile war:war -f pom.xml
mvn compile war:exploded -f pom.xml

docker compose up
