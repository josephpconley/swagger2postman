package com.josephpconley.swagger2postman.app.v2

import java.io.PrintWriter
import java.net.URL

import com.josephpconley.swagger2postman.CollectionArgs
import com.josephpconley.swagger2postman.models.swagger._
import com.stackmob.newman._
import com.stackmob.newman.dsl._
import play.api.libs.json._

import scala.concurrent._
import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps
import scala.util.Try

object Swagger2PostmanApp
  extends v2.Swagger2Postman
  with App {
  implicit val httpClient = new ApacheHttpClient

  if(args.length < 2){
    throw new IllegalArgumentException("Invalid number of arguments, must be <filename> <collectionName> [<key=value> ... ]")
  }

  val headerMap = args.drop(2) map { kv =>
    val h = kv.split("=")
    h.head -> h.last
  } toMap

  val cArgs = CollectionArgs(host = "host", name = args(1), headers = headerMap)

  val fileJson = Source.fromFile(args(0)).getLines.mkString
  Json.fromJson[v2.SwaggerDoc](Json.parse(fileJson)).fold(
    invalid => {
      println("Error converting Swagger v2 doc to Postman json")
      println(JsError.toFlatJson(invalid))
    },
    swaggerDoc => {
      val postmanJson = toPostman(swaggerDoc, cArgs)
      println(postmanJson)

      val writer = new PrintWriter("postman.json", "UTF-8")
      writer.append(Json.prettyPrint(postmanJson))
      writer.close()
    }
  )
}
