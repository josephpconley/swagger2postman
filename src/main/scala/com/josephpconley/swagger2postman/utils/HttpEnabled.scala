package com.josephpconley.swagger2postman.utils

trait HttpEnabled {

  def execute(url: String): String
}
