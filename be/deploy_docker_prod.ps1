$HCPOSPOC_RESOURCE_GROUP = "HCIN"
$HCPOSPOC_PERS_STORAGE_NAME = "axelorstorage"
$HCPOSPOC_PERS_STORAGE_LOCATION = "centralindia"
$HCPOSPOC_PERS_STORAGE_SHARE_NAME = "hcpospocshare"
$HCPOSPOC_PERS_STORAGE_MOUNT_PATH = "/var/lib/tomcat"
$HCPOSPOC_PERS_STORAGE_MOUNT_PATH2 = "/var/lib/postgresql"
$HCPOSPOC_PERS_STORAGE_MOUNT_PATH3 = "/var/log/postgresql"
$HCPOSPOC_PERS_STORAGE_MOUNT_PATH4 = "/var/log/tomcat"

$HCPOSPOC_CONTAINER_NAME = "axelor-main"
$HCPOSPOC_AXELOR_IMAGE_NAME = "hcpospoc.azurecr.io/hcpospocaxelor"
$HCPOSPOC_CONTAINER_REGISTRY_SERVER_NAME = "hcpospoc.azurecr.io"
$HCPOSPOC_CONTAINER_REGISTRY_USERNAME = "hcpospoc"

$HCPOSPOC_DNS_LABEL = "axelorhcpospoc"

#DONE
Write-Host "Starting docker-machine for good measure"
docker-machine start

#shouldnt be called - how
#Write-Host "Login to Azure"
#az login

#DONE
Write-Host "Login to acr"
az acr login --name $HCPOSPOC_CONTAINER_REGISTRY_USERNAME

#DONE
Write-Host "Create storage account"
az storage account create --resource-group $HCPOSPOC_RESOURCE_GROUP --name $HCPOSPOC_PERS_STORAGE_NAME --location $HCPOSPOC_PERS_STORAGE_LOCATION --sku Standard_LRS

#DONE
Write-Host "Exporting storage account connection string"
$AZURE_STORAGE_CONNECTION_STRING=(az storage account show-connection-string --resource-group $HCPOSPOC_RESOURCE_GROUP --name $HCPOSPOC_PERS_STORAGE_NAME --output tsv)
Write-Host $AZURE_STORAGE_CONNECTION_STRING

#DONE
#Write-Host "Creating storage file share"
#az storage share create -n $HCPOSPOC_PERS_STORAGE_SHARE_NAME --connection-string $AZURE_STORAGE_CONNECTION_STRING

#DONE
Write-Host "Storage account set"
$storage_account=(az storage account list --resource-group $HCPOSPOC_RESOURCE_GROUP --query "[?contains(name, '$HCPOSPOC_PERS_STORAGE_NAME')].[name]" --output tsv)
Write-Host $storage_account

#DONE
Write-Host "Printing Storage Key"
$storage_key=(az storage account keys list --resource-group $HCPOSPOC_RESOURCE_GROUP --account-name $storage_account --query "[0].value" --output tsv)
Write-Host $storage_key

#DONE
Write-Host "Pushing the docker image to Azure"
docker push $HCPOSPOC_AXELOR_IMAGE_NAME

#DONE
Write-Host "Checking if the image is there..."
az acr repository list --name $HCPOSPOC_CONTAINER_REGISTRY_USERNAME --output table

#DONE
$acr_password=(az acr credential show --name $HCPOSPOC_CONTAINER_REGISTRY_USERNAME --query passwords[0].value)
Write-Host "ACR PASSWORD is:"
Write-Host $acr_password

#DONE
Write-Host "Creating Azure Container Instance"
#az container create --resource-group $HCPOSPOC_RESOURCE_GROUP --name $HCPOSPOC_CONTAINER_NAME --image $HCPOSPOC_AXELOR_IMAGE_NAME --cpu 1 --memory 1 --registry-login-server $HCPOSPOC_CONTAINER_REGISTRY_SERVER_NAME --registry-username $HCPOSPOC_CONTAINER_REGISTRY_USERNAME --registry-password $acr_password --dns-name-label $HCPOSPOC_DNS_LABEL --ports 80 8080 8081 443 --azure-file-volume-account-name $HCPOSPOC_PERS_STORAGE_NAME --azure-file-volume-account-key $storage_key --azure-file-volume-share-name $HCPOSPOC_PERS_STORAGE_SHARE_NAME --azure-file-volume-mount-path $HCPOSPOC_PERS_STORAGE_MOUNT_PATH

#az container create --resource-group $HCPOSPOC_RESOURCE_GROUP -f azure_axelor_prod.json

az group deployment create --resource-group HCIN --template-file azure_axelor_prod.json


Write-Host "Checking Container Status"

$container_status = (az container show --resource-group HCIN --name axelor-be --query instanceView.state)

Write-Host $container_status

Write-Host "Where to find fileshare"
Write-Host "Alternatively you can use https://azure.microsoft.com/en-us/features/storage-explorer/"

