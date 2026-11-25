variable "location" {
  description = "Região da Azure onde os recursos serão criados"
  type        = string
  default     = "eastus"
}

variable "resource_group_name" {
  description = "Nome do Resource Group principal do projeto"
  type        = string
  default     = "ironforge-rg"
}

variable "vm_size" {
  description = "SKU das máquinas virtuais (VM de apoio e nós do AKS)"
  type        = string
  default     = "Standard_D2ls_v6"
}

variable "admin_username" {
  description = "Usuário administrador da VM Linux"
  type        = string
  default     = "ironforgeadm"
}

variable "admin_password" {
  description = "Senha do administrador da VM Linux"
  type        = string
  sensitive   = true
}

variable "aks_cluster_name" {
  description = "Nome do cluster AKS"
  type        = string
  default     = "ironforge-aks"
}

variable "aks_node_count" {
  description = "Quantidade de nós no pool padrão do AKS (1 nó = 2 vCPUs / 4 GB RAM)"
  type        = number
  default     = 1
}

variable "acr_name" {
  description = "Nome globalmente único do Azure Container Registry (apenas letras minúsculas e números)"
  type        = string
  default     = "ironforgeacrhnq26"
}