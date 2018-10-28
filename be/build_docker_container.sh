#!/bin/bash


DOCKER_IMAGE_TAG="hcpospoc.azurecr.io/hcpospocaxelor"




echo ""
echo ""
echo ""
echo "########################################"
echo "###  Building Axelor app via Gradle  ###"
echo "########################################"
echo ""
echo ""
echo ""
./gradlew -x test build

echo ""
echo ""
echo ""
echo "##########################################"
echo "###  Building RestConnector via Maven  ###"
echo "##########################################"
echo ""
echo ""
echo ""
mvn install -f ../restconnect/pom.xml
cp ../restconnect/target/*.war .

echo ""
echo ""
echo ""
echo "#################################"
echo "###  Starting Docker Machine  ###"
echo "#################################"
echo ""
echo ""
echo ""
docker-machine start

echo ""
echo ""
echo ""
echo "###############################"
echo "###  Building Docker Image  ###"
echo "###############################"
echo ""
echo ""
echo ""
docker build -t $DOCKER_IMAGE_TAG .

echo ""
echo ""
echo ""
echo "##################"
echo "###  Cleaning  ###"
echo "##################"
echo ""
echo ""
echo ""
rm ./*.war
