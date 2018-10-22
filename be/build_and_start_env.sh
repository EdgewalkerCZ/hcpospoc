#!/bin/bash

#./gradlew -x test build

docker-machine start

#winpty docker build -t hcin/hcpospoc .

for i in 10; do
    echo "###"
done
echo "#####################"
echo "DOCKER IS RUNNING ON:"
echo `docker-machine ip`
echo "#####################"
for i in 10; do
    echo "###"
done

#winpty docker run -it -p 8080:80 hcin/hcpospoc
 
