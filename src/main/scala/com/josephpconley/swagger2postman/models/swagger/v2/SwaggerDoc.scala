package com.josephpconley.swagger2postman.models.swagger.v2

import play.api.libs.json.Json

case class SwaggerDoc(
  swagger: String,
  info: SwaggerInfo,
  host: String,
  basePath: String,
  tags: Seq[SwaggerTag],
  paths: Map[String, Map[String, SwaggerPath]])

object SwaggerDoc {
  implicit val paramFmt = Json.format[SwaggerParam]
  implicit val pathFmt = Json.format[SwaggerPath]
  implicit val tagFmt = Json.format[SwaggerTag]
  implicit val infoFmt = Json.format[SwaggerInfo]
  implicit val docFmt = Json.format[SwaggerDoc]
}

case class SwaggerInfo(description: Option[String], version: String, title: String)

case class SwaggerTag(name: String, description: Option[String])

case class SwaggerPath(tags: Seq[String], summary: String, description: String, operationId: String, parameters: Seq[SwaggerParam])

case class SwaggerParam(in: String, name: String, description: Option[String], required: Boolean)
