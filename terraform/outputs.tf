output "CatInfoSalutAPI" {
  value = {
    base_url = aws_apigatewayv2_stage.defaultStage.invoke_url
  }
}
