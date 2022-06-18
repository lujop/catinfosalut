terraform {
  cloud {
    organization = "lujop"
    workspaces {
      name = "catinfosalut"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

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

  #To trigger/prevent updates
  source_code_hash = filebase64sha256("function.zip")

  runtime = "provided.al2"
  handler = "io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest"
  environment {
    variables = {
      DISABLE_SIGNAL_HANDLERS = true
    }
  }
}
