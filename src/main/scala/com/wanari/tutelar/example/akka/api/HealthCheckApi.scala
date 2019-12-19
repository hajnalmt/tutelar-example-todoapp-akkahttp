package com.wanari.tutelar.example.akka.api

import akka.http.scaladsl.server.Directives.{complete, get, path}
import com.wanari.tutelar.example.akka.api.HealthCheckApi.HealthCheckDto
import spray.json.RootJsonFormat

class HealthCheckApi {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  val routes = (get & path("healthCheck")) {
    complete(
      HealthCheckDto(
        true
      )
    )
  }
}

object HealthCheckApi {
  import spray.json.DefaultJsonProtocol._
  implicit val healthCheckDtoFormat: RootJsonFormat[HealthCheckDto] = jsonFormat1(HealthCheckDto)
  case class HealthCheckDto(
    success: Boolean,
  )
}
