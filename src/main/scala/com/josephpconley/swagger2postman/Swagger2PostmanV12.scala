package com.josephpconley.swagger2postman

import java.io.PrintWriter
import java.net.URL

import com.josephpconley.swagger2postman.models.swagger._
import com.stackmob.newman._
import com.stackmob.newman.dsl._
import play.api.libs.json._

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps

object Swagger2PostmanV12
  extends v12.Swagger2Postman
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

  val res = execute(cArgs.docUrl)
  val swaggerDoc = Json.fromJson[v12.SwaggerDoc](Json.parse(res)).get
  val postmanJson = toPostman(swaggerDoc, cArgs)

  println(postmanJson)
  val writer = new PrintWriter("postman.json", "UTF-8")
  writer.append(Json.prettyPrint(postmanJson))
  writer.close()

  def execute(url: String) = Await.result(GET(new URL(url)).apply, Duration.Inf).bodyString
}