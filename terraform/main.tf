terraform {
  #  cloud {
  #    organization = "lujop"
  #    workspaces {
  #      name = "catinfosalut"
  #    }
  #  }
  required_providers {
    http-full = {
      source  = "salrashid123/http-full"
      version = "1.2.8"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# Module to deploy lambda function containing application
module "app_function" {
  source = "./modules/app_function"

  aws_account_id = var.aws_account_id
  aws_region     = var.aws_region
}

#Module to setup ssm parameters and configure deployed endpoint as webhookurl in telegram
module "configure_application" {
  source           = "./modules/configure_application"
  function_api_url = module.app_function.api_url
  depends_on       = [module.app_function]

  telegram_token = var.telegram_token
}
