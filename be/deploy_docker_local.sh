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

#Make sure we are not running the container yet


winpty docker run -it -p 8080:8080 -p 80:80 -p 8081:8081  hcpospoc.azurecr.io/hcpospoc:test
