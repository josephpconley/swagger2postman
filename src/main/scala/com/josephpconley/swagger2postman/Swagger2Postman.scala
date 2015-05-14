package com.josephpconley.swagger2postman

import java.io.PrintWriter

import com.josephpconley.swagger2postman.models._
import com.stackmob.newman._
import com.stackmob.newman.dsl._
import scala.concurrent._
import scala.concurrent.duration._
import java.net.URL

import play.api.libs.json._
import scala.language.postfixOps
import scala.util.Try

object Swagger2PostmanApp
  extends Swagger2Postman
  with App {

  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val httpClient = new ApacheHttpClient

  if(args.length < 2){
    throw new IllegalArgumentException("Invalid number of arguments, must be <host> <collectionName> [<key=value> ... ]")
  }

  val headerMap = args.drop(2) map { kv =>
    val h = kv.split("=")
    h.head -> h.last
  } toMap

  val cArgs = CollectionArgs(host = args(0), name = args(1), headers = headerMap)
  val postmanJson = generate(cArgs)

  println(postmanJson)
  val writer = new PrintWriter("postman.json", "UTF-8")
  writer.append(Json.prettyPrint(postmanJson))
  writer.close()

  def execute(url: String) = Await.result(GET(new URL(url)).apply, Duration.Inf).bodyString
}

trait Swagger2Postman
  extends SwaggerFormats
  with PostmanFormats {

  def execute(url: String): String

  def generate(cArgs: CollectionArgs): JsValue = {

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