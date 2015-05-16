package com.josephpconley.swagger2postman

import play.api.libs.json.Json

import ConversionUtils._

case class CollectionArgs(host: String, name: String, headers: Map[String, String] = Map.empty){
  val docUrl = host + "/api-docs"
  val owner = "12345"
  val collectionId = genUUID
}

trait CollectionFormats {
  implicit val collectionFmt = Json.format[CollectionArgs]
}
