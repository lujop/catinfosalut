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

###
### Function deployment
###
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

###
### API Gateway integration with function
###
resource "aws_api_gateway_rest_api" "CatInfoSalutAPI" {
  name = "CatInfoSalutAPI"
}

resource "aws_api_gateway_resource" "hello" {
  parent_id   = aws_api_gateway_rest_api.CatInfoSalutAPI.root_resource_id
  path_part   = "resource"
  rest_api_id = aws_api_gateway_rest_api.CatInfoSalutAPI.id
}

resource "aws_api_gateway_method" "method" {
  authorization = "NONE"
  http_method   = "GET"
  rest_api_id   = aws_api_gateway_rest_api.CatInfoSalutAPI.id
  resource_id   = aws_api_gateway_resource.hello.id
}

resource "aws_api_gateway_integration" "integration" {
  rest_api_id             = aws_api_gateway_rest_api.CatInfoSalutAPI.id
  resource_id             = aws_api_gateway_resource.hello.id
  http_method             = aws_api_gateway_method.method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.catinfosalut.invoke_arn
}

resource "aws_lambda_permission" "api_gateway_catinfosalut" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.catinfosalut.function_name
  principal     = "apigateway.amazonaws.com"

  # More: http://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-control-access-using-iam-policies-to-invoke-api.html
  source_arn = "arn:aws:execute-api:${var.aws_region}:${var.aws_account_id}:${aws_api_gateway_rest_api.CatInfoSalutAPI.id}/*/${aws_api_gateway_method.method.http_method}${aws_api_gateway_resource.hello.path}"
}

