package com.josephpconley.swagger2postman.models.swagger.v2

import com.josephpconley.swagger2postman.CollectionArgs
import com.josephpconley.swagger2postman.models.postman._
import com.josephpconley.swagger2postman.utils.ConversionUtils

import play.api.libs.json.{JsValue, Json}

import ConversionUtils._

trait Swagger2Postman extends PostmanFormats {

  def toPostman(swaggerDoc: SwaggerDoc, cArgs: CollectionArgs): JsValue = {

    val requests: Seq[(String, PostmanRequest)] =
      for {
        (path, operations) <- swaggerDoc.paths.toSeq
        (method, operation) <- operations.toSeq
      } yield {

        val queryParams = operation.parameters.map { params =>
          params.filter(_.in == "query") match {
            case Nil => ""
            case list => "?" + list.map(_.name + "=").mkString("&")
          }
        }.getOrElse("")

        //TODO remove double slash
        operation.tags.head -> PostmanRequest(
          id = genUUID,
          url = s"http://${swaggerDoc.host}${swaggerDoc.basePath}$path$queryParams",
          headers = cArgs.headers map (h => s"${h._1}: ${h._2}") mkString "\n",
          method = method,
          rawModeData = None,
          //dataMode = bodyOpt map (_ => "raw") getOrElse "params",
          name = operation.operationId,
          description = operation.summary + "\r\n" + operation.description.getOrElse(""),
          collectionId = cArgs.collectionId
        )
      }

    val folders = swaggerDoc.tags map { tag =>
      PostmanFolder(
        id = genUUID,
        name = tag.name,
        description = tag.description.getOrElse(""),
        order = requests filter (_._1 == tag.name) map (_._2.id),
        collection_name = cArgs.name,
        collection_owner = cArgs.owner,
        collection_id = cArgs.collectionId,
        collection = cArgs.collectionId,
        owner = cArgs.owner
      )
    }

    val postmanCollection = PostmanCollection(
      id = cArgs.collectionId,
      name = cArgs.name,
      folders = folders,
      owner = cArgs.owner,
      requests = requests.map(_._2)
    )

    Json.toJson(postmanCollection)
  }
}
