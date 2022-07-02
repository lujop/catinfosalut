###
### Lambda execution permissions
###
#Define assume_role policy
data "aws_iam_policy_document" "assume_role" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }
  }
}
#lambda execution role
resource "aws_iam_role" "lambda_exec" {
  name = "serverless_lambda"

  assume_role_policy = data.aws_iam_policy_document.assume_role.json
}
# Attach policy to be able log
resource "aws_iam_role_policy_attachment" "basic" {
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
  role       = aws_iam_role.lambda_exec.name
}
# Attach policy to use XRay
resource "aws_iam_role_policy_attachment" "aws_xray_write_only_access" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = "arn:aws:iam::aws:policy/AWSXrayWriteOnlyAccess"
}
# Attach policy to read SSM parameters
resource "aws_iam_role_policy_attachment" "aws_ssm_read_only_access" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMReadOnlyAccess"
}


###
### Lambda function with rest application
###
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
      telegram_token          = aws_ssm_parameter.telegram_token.name
    }
  }
}


###
### API Gateway integration with function
###
resource "aws_apigatewayv2_api" "catinfosalut" {
  name          = "CatInfoSalutAPI"
  protocol_type = "HTTP"
}
resource "aws_apigatewayv2_stage" "default_stage" {
  api_id      = aws_apigatewayv2_api.catinfosalut.id
  name        = "$default"
  auto_deploy = true
}
resource "aws_apigatewayv2_integration" "integration" {
  api_id           = aws_apigatewayv2_api.catinfosalut.id
  integration_type = "AWS_PROXY"

  integration_uri        = aws_lambda_function.catinfosalut.invoke_arn
  integration_method     = "POST"
  payload_format_version = "2.0"
}
resource "aws_apigatewayv2_route" "default_route" {
  api_id    = aws_apigatewayv2_api.catinfosalut.id
  route_key = "$default"

  target = "integrations/${aws_apigatewayv2_integration.integration.id}"
}
resource "aws_lambda_permission" "api_gateway_catinfosalut" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.catinfosalut.function_name
  principal     = "apigateway.amazonaws.com"

  source_arn = "arn:aws:execute-api:${var.aws_region}:${var.aws_account_id}:${aws_apigatewayv2_api.catinfosalut.id}/*/*"
}
