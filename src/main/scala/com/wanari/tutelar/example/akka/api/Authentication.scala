package com.wanari.tutelar.example.akka.api

import akka.http.scaladsl.server.{Directive0, Directive1}
import akka.http.scaladsl.server.Directives.authenticateOAuth2
import akka.http.scaladsl.server.directives.Credentials
import com.wanari.tutelar.example.akka.api.Authentication.JwtPayload
import com.wanari.tutelar.example.akka.utils.Config.JwtConfig
import pdi.jwt.{JwtAlgorithm, JwtSprayJson}
import spray.json.RootJsonFormat

trait Authentication {

  def jwtConfig: JwtConfig

  def authenticated: Directive0 = authenticatedWithPayload.map(_ => {})
  def authenticatedWithPayload: Directive1[JwtPayload] =
    authenticateOAuth2[JwtPayload](realm = "", authenticateCredentials)

  protected def authenticateCredentials(credentials: Credentials): Option[JwtPayload] = {
    credentials match {
      case Credentials.Provided(token) =>
        validateAndDecode(token)
      case Credentials.Missing =>
        None
    }
  }

  protected def validateAndDecode(token: String): Option[JwtPayload] = {
    JwtSprayJson
      .decodeJson(token, jwtConfig.publicKey, Seq(JwtAlgorithm.HS256))
      .map{x => println(x); x.convertTo[JwtPayload]}
      .toOption
  }
}

object Authentication {

  case class JwtPayload(
    id: String
  )

  import spray.json.DefaultJsonProtocol._
  implicit val jwtPayloadFormatter: RootJsonFormat[JwtPayload] = jsonFormat1(JwtPayload)
}
