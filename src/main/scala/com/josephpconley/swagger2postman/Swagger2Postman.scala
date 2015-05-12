package com.josephpconley.swagger2postman

import com.josephpconley.swagger2postman.models._
import com.stackmob.newman._
import com.stackmob.newman.dsl._
import scala.concurrent._
import scala.concurrent.duration._
import java.net.URL

import play.api.libs.json._

object Swagger2PostmanApp
  extends Swagger2Postman
  with App {

  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val httpClient = new ApacheHttpClient

  val cArgs = CollectionArgs(name = "backoffice", host = "http://localhost:8080", headers = Map("X-AL-API-AuthToken" -> "adminToken"))
  println(generate(cArgs))

  def execute(url: String) = Await.result(GET(new URL(url)).apply, Duration.Inf).bodyString
}

trait Swagger2Postman
  extends SwaggerFormats
  with PostmanFormats {

  def execute(url: String): String

  def generate(cArgs: CollectionArgs): JsValue = {

    println(cArgs)

    val docUrl = cArgs.host + "/api-docs"
    val owner = "12345"
    val collectionId = genUUID

    val res = execute(docUrl)
    val swaggerDoc = Json.fromJson[SwaggerDoc](Json.parse(res)).get

    val requests = swaggerDoc.apis map { api =>
      val apiRes = execute(docUrl + api.path)
      val swaggerApi = Json.fromJson[SwaggerApi](Json.parse(apiRes)).get

      val requests =
        for {
          endpoint <- swaggerApi.apis
          operation <- endpoint.operations
        } yield {
          PostmanRequest(
            id = genUUID,
            url = cArgs.host + endpoint.path,
            headers = cArgs.headers map (h => s"${h._1}: ${h._2}") mkString "\n",
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
          collection_name = cArgs.name,
          collection_owner = owner,
          collection_id = collectionId,
          collection = collectionId,
          owner = owner
        )

      folder -> requests
    }

    val postmanCollection = PostmanCollection(
      id = collectionId,
      name = cArgs.name,
      folders = requests.map(_._1),
      owner = owner,
      requests = requests.map(_._2).flatten
    )

    Json.toJson(postmanCollection)
  }

  def genUUID = java.util.UUID.randomUUID.toString
}