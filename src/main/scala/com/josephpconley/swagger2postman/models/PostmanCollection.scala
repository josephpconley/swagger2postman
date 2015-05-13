package com.josephpconley.swagger2postman.models

import org.joda.time.DateTime
import play.api.libs.json._

case class PostmanCollection(
  id: String,
  name: String,
  description: String = "",
  order: Seq[String] = Seq.empty[String],
  folders: Seq[PostmanFolder],
  timestamp: Long = DateTime.now().getMillis,
  synced: Boolean = false,
  owner: String,
  sharedWithTeam: Boolean = false,
  subscribed: Boolean = false,
  remoteLink: String = "",
  public: Boolean = false,
  write: Boolean = true,
  requests: Seq[PostmanRequest])

case class PostmanFolder(
  id: String,
  name: String,
  description: String,
  write: Boolean = true,
  order: Seq[String],
  collection_name: String,
  collection_owner: String,
  collection_id: String,
  collection: String,
  owner: String)

case class PostmanRequest(
  id: String,
  headers: String = "",
  url: String,
  preRequestScript: String = "",
  pathVariables: JsObject = Json.obj(),
  method: String,
  data: JsArray = Json.arr(),
  dataMode: String = "params",
  rawModeData: Option[String] = None,
  version: Int = 2,
  tests: String = "",
  currentHelper: String = "normal",
  helperAttributes: JsObject = Json.obj(),
  time: Long = DateTime.now().getMillis,
  name: String,
  description: String,
  collectionId: String,
  responses: JsArray = Json.arr(),
  synced: Boolean = false)

trait PostmanFormats {
  implicit val reqFmt = Json.format[PostmanRequest]
  implicit val folderFmt = Json.format[PostmanFolder]
  implicit val collFmt = Json.format[PostmanCollection]
}