output "vm_public_ip" {
  description = "IP Público da VM de apoio (SSH e Ansible)"
  value       = azurerm_linux_virtual_machine.vm.public_ip_address
}

output "vm_private_ip" {
  description = "IP Privado fixo da VM de apoio (Postgres, Kafka, Keycloak, MinIO)"
  value       = azurerm_linux_virtual_machine.vm.private_ip_address
}

output "acr_login_server" {
  description = "Endereço do Registry para push/pull das imagens Docker"
  value       = azurerm_container_registry.acr.login_server
}

output "aks_cluster_name" {
  description = "Nome do cluster AKS"
  value       = azurerm_kubernetes_cluster.aks.name
}

output "aks_connect_command" {
  description = "Comando para conectar o kubectl ao AKS"
  value       = "az aks get-credentials --resource-group ${azurerm_resource_group.rg.name} --name ${azurerm_kubernetes_cluster.aks.name} --overwrite-existing"
}