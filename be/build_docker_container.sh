#!/bin/bash

./gradlew -x test build

docker-machine start

winpty docker build -t hcpospoc.azurecr.io/hcpospoc:test .

 
