variable "aws_region" {
  description = "AWS region"
  type        = string
}

variable "aws_account_id" {
  description = "AWS account Id"
  type        = string
}

variable "telegram_token" {
  description = "Bot Telegram secret token"
  type        = string
  sensitive   = true
}
