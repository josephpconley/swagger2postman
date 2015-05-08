package com.josephpconley.swagger2postman

import com.josephpconley.swagger2postman.models._
import com.stackmob.newman._
import com.stackmob.newman.dsl._
import scala.concurrent._
import scala.concurrent.duration._
import java.net.URL

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._

object Swagger2Postman extends App with SwaggerFormats with PostmanFormats {

  implicit val httpClient = new ApacheHttpClient
  
  val host = "http://bo3backoffice-api-dev.angieslist.com:8080" 
  val docUrl = host + "/api-docs"
  val name = "backoffice"
  val owner = "12345"
  val collectionId = genUUID

  val headers = Seq(
    "X-AL-API-AuthToken" -> "957e7fa7-7216-4f3b-8100-9be1a5320808"
  )

  val res = Await.result(GET(new URL(docUrl)).apply, Duration.Inf)
  val swaggerDoc = Json.fromJson[SwaggerDoc](Json.parse(res.bodyString)).get

  val requests = swaggerDoc.apis map { api =>
    val res = Await.result(GET(new URL(docUrl + api.path)).apply, Duration.Inf)
    val swaggerApi = Json.fromJson[SwaggerApi](Json.parse(res.bodyString)).get

    val requests =
      for {
        endpoint <- swaggerApi.apis
        operation <- endpoint.operations
      } yield {
        PostmanRequest(
          id = genUUID,
          url = host + endpoint.path,
          headers = headers map (h => s"${h._1}: ${h._2}") mkString "\n",
          method = operation.method,
          name = operation.nickname,
          description = operation.notes,
          collectionId = collectionId
        )
      }

    val folder =
      PostmanFolder(
        id = genUUID,
        name = api.path,
        description = api.description,
        order = requests map (_.id),
        collection_name = name,
        collection_owner = owner,
        collection_id = collectionId,
        collection = collectionId,
        owner = owner
      )

    folder -> requests
  }

  val postmanCollection = PostmanCollection(
    id = collectionId,
    name = name,
    folders = requests.map(_._1),
    owner = owner,
    requests = requests.map(_._2).flatten
  )

  println(Json.toJson(postmanCollection))

  def genUUID = java.util.UUID.randomUUID.toString
}