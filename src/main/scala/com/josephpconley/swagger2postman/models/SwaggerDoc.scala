package com.josephpconley.swagger2postman.models

import play.api.libs.json.Json

case class SwaggerDoc(
  apiVersion: String,
  swaggerVersion: String,
  apis: Seq[SwaggerApiPath])

case class SwaggerApiPath(
  path: String,
  description: String)

case class SwaggerApi(
  apiVersion: String,
  swaggerVersion: String,
  basePath: String,
  resourcePath: String,
  apis: Seq[SwaggerEndpoint])

case class SwaggerEndpoint(
  path: String,
  operations: Seq[SwaggerOperation])

case class SwaggerOperation(
  method: String,
  summary: String,
  notes: String,
  nickname: String)

trait SwaggerFormats {
  implicit val opFmt = Json.format[SwaggerOperation]
  implicit val endpointFmt = Json.format[SwaggerEndpoint]
  implicit val apiFmt = Json.format[SwaggerApi]
  implicit val apiPathFmt = Json.format[SwaggerApiPath]
  implicit val docFmt = Json.format[SwaggerDoc]
}