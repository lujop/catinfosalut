terraform {
  required_providers {
    http-full = {
      source  = "salrashid123/http-full"
      version = "1.2.8"
    }
  }
}

#Set telegram secret token in SSM
resource "aws_ssm_parameter" "telegram_token" {
  name      = "telegram_token"
  type      = "SecureString"
  value     = var.telegram_token
  overwrite = true
}

#Create random string as private_api_secret (token needed to set webhook)
resource "random_password" "private_api_secret" {
  length = 32
}

resource "aws_ssm_parameter" "private_api_secret" {
  name  = "private_api_secret"
  type  = "SecureString"
  value = random_password.private_api_secret.result
  overwrite = true
}

resource "aws_ssm_parameter" "api_url" {
  name  = "api_url"
  type  = "SecureString"
  value = var.function_api_url
}

# Setup telegram webhook using our private api telegram/setupWebhook
data "http" "setupWebhook" {
  provider = http-full
  url      = "${var.function_api_url}telegram/setupWebhook"
  method   = "POST"

  request_headers = {
    PrivateApiSecret = urlencode(aws_ssm_parameter.private_api_secret.value)
    Content-Type     = "application/x-www-form-urlencoded"
  }

  request_body = "baseUrl=${urlencode(var.function_api_url)}"
}


