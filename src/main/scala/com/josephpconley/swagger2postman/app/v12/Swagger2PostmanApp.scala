package com.josephpconley.swagger2postman.app.v12

import java.io.PrintWriter
import java.net.URL

import com.josephpconley.swagger2postman.CollectionArgs
import com.josephpconley.swagger2postman.models.swagger._
import com.stackmob.newman._
import com.stackmob.newman.dsl._
import play.api.libs.json._

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try

object Swagger2PostmanApp
  extends v12.Swagger2Postman
  with v2.Swagger2Postman
  with App {
  implicit val httpClient = new ApacheHttpClient

  if(args.length < 2){
    throw new IllegalArgumentException("Invalid number of arguments, must be <host> <collectionName> [<key=value> ... ]")
  }

  val headerMap = args.drop(2) map { kv =>
    val h = kv.split("=")
    h.head -> h.last
  } toMap

  val cArgs = CollectionArgs(host = args(0), name = args(1), headers = headerMap)

  val postmanJson = Try(execute(cArgs.docUrl)) map { res =>
    val swaggerDoc = Json.fromJson[v12.SwaggerDoc](Json.parse(res)).get
    toPostman(swaggerDoc, cArgs)
  } getOrElse {
    Try(execute(cArgs.host)) map { res =>
      val swaggerDoc = Json.fromJson[v2.SwaggerDoc](Json.parse(res)).get
      toPostman(swaggerDoc, cArgs)
    } getOrElse {
      throw new RuntimeException("Unable to reach Swagger 1.2 and 2.0 endpoints.")
    }
  }

  println(postmanJson)
  val writer = new PrintWriter("postman.json", "UTF-8")
  writer.append(Json.prettyPrint(postmanJson))
  writer.close()

  def execute(url: String) = Await.result(GET(new URL(url)).apply, Duration.Inf).bodyString
}