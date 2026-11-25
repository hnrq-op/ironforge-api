terraform {
  required_version = ">= 1.3.0"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 3.100.0"
    }
    local = {
      source  = "hashicorp/local"
      version = ">= 2.4.0"
    }
  }

  backend "azurerm" {
    resource_group_name  = "ironforge-tfstate-rg"
    storage_account_name = "ironforgetfstatehnq26"
    container_name       = "tfstate"
    key                  = "ironforge.terraform.tfstate"
  }
}