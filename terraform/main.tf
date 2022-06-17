#terraform {
#  required_providers {
#    aws = {
#      source  = "hashicorp/aws"
#      version = "~> 4.0.0"
#    }
#    random = {
#      source  = "hashicorp/random"
#      version = "~> 3.1.0"
#    }
#    archive = {
#      source  = "hashicorp/archive"
#      version = "~> 2.2.0"
#    }
#  }
#
#  required_version = "~> 1.0"
#}

provider "aws" {
  region = var.aws_region
}

## Create bucket to upload lambda
#resource "random_pet" "lambda_bucket_name" {
#  prefix = "catinfosalut"
#  length = 4
#}
#resource "aws_s3_bucket" "lambda_bucket" {
#  bucket = random_pet.lambda_bucket_name.id
#  aws_s3_bucket_acl = "private"
#  force_destroy = true
#}
#resource "aws_s3_bucket_acl" "lambda_bucket_acl" {
#  bucket = aws_s3_bucket.lambda_bucket.id
#  acl    = "private"
#}
#

resource "aws_iam_role" "lambda_exec" {
  name = "serverless_lambda"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Sid    = ""
      Principal = {
        Service = "lambda.amazonaws.com"
      }
      }
    ]
  })
}

resource "aws_lambda_function" "catinfosalut" {
  filename      = "function.zip"
  function_name = "hello_function"
  role          = aws_iam_role.lambda_exec.arn

  #To trigger updates
  source_code_hash = filebase64sha256("function.zip")

  runtime = "provided.al2"
  handler = "io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest"
  environment {
    variables = {
      DISABLE_SIGNAL_HANDLERS = true
    }
  }
}
