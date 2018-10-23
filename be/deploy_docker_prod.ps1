Write-Host "Starting docker-machine for good measure"
docker-machine start

Write-Host "Login to acr"
az acr login --name hcpospoc

Write-Host "Pushing the docker image to Azure"
docker push hcpospoc.azurecr.io/hcpospoc:test

Write-Host "Checking if the image is there..."
az acr repository list --name hcpospoc --output table

$acr_password = (az acr credential show --name hcpospoc --query passwords[0].value)

Write-Host "ACR PASSWORD is:"
Write-Host $acr_password

Write-Host "Creating Azure Container Instance"
az container create --resource-group HCIN --name hcin-hcpospoc --image hcpospoc.azurecr.io/hcpospoc:test --cpu 1 --memory 1 --registry-login-server hcpospoc.azurecr.io --registry-username hcpospoc --registry-password $acr_password --dns-name-label hcinhcpospoc --ports 80

Write-Host "Checking Container Status"

$container_status = (az container show --resource-group HCIN --name hcin-hcpospoc --query instanceView.state)

Write-Host $container_status

