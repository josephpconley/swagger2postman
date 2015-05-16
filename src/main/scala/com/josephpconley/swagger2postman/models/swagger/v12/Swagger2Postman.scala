package com.josephpconley.swagger2postman.models.swagger.v12

import com.josephpconley.swagger2postman.{ConversionUtils, CollectionArgs}
import com.josephpconley.swagger2postman.models.postman._
import play.api.libs.json.{Json, JsValue}

import scala.util.Try

import ConversionUtils._

trait Swagger2Postman
  extends SwaggerFormats
  with PostmanFormats {

  def execute(url: String): String

  def toPostman(swaggerDoc: SwaggerDoc, cArgs: CollectionArgs): JsValue = {

    val requests = swaggerDoc.apis map { api =>
      val apiRes = execute(cArgs.docUrl + api.path)
      val swaggerApi = Json.fromJson[SwaggerApi](Json.parse(apiRes)).get

      val requests =
        for {
          endpoint <- swaggerApi.apis
          operation <- endpoint.operations
        } yield {

          val queryParams = operation.parameters filter (_.paramType == "query") match {
            case Nil => ""
            case list => "?" + list.map(_.name + "=").mkString("&")
          }

          val bodyOpt = operation.parameters find (_.paramType == "body") flatMap (_.defaultValue)
          val headers: Map[String, String] =
            if(bodyOpt.isDefined && Try(Json.parse(bodyOpt.get)).isSuccess){
              Map("Content-Type" -> "application/json") ++ cArgs.headers
            } else {
              cArgs.headers
            }

          PostmanRequest(
            id = genUUID,
            url = cArgs.host + endpoint.path + queryParams,
            headers = headers map (h => s"${h._1}: ${h._2}") mkString "\n",
            method = operation.method,
            rawModeData = bodyOpt,
            dataMode = bodyOpt map (_ => "raw") getOrElse "params",
            name = operation.nickname,
            description = operation.notes,
            collectionId = cArgs.collectionId
          )
        }

      val folder =
        PostmanFolder(
          id = genUUID,
          name = api.path,
          description = api.description,
          order = requests map (_.id),
          collection_name = cArgs.name,
          collection_owner = cArgs.owner,
          collection_id = cArgs.collectionId,
          collection = cArgs.collectionId,
          owner = cArgs.owner
        )

      folder -> requests
    }

    val postmanCollection = PostmanCollection(
      id = cArgs.collectionId,
      name = cArgs.name,
      folders = requests.map(_._1),
      owner = cArgs.owner,
      requests = requests.map(_._2).flatten
    )

    Json.toJson(postmanCollection)
  }
}