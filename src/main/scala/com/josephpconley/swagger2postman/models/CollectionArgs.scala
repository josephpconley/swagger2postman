package com.josephpconley.swagger2postman.models

import play.api.libs.json.Json

case class CollectionArgs(host: String, name: String, headers: Map[String, String] = Map.empty)

trait CollectionFormats {
  implicit val collectionFmt = Json.format[CollectionArgs]
}
