package com.wanari.tutelar.example.akka.utils

import com.wanari.tutelar.example.akka.utils.Config.{Database, JwtConfig}
import pureconfig.generic.ProductHint

class Config {
  import pureconfig._
  import pureconfig.generic.auto._

  implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  val db: Database           = ConfigSource.default.at("db").loadOrThrow[Database]
  val jwtConfig: JwtConfig   = ConfigSource.default.at("jwt").loadOrThrow[JwtConfig]
}

object Config {

  case class Database(url: String, user: String, password: String, forceMigration: Boolean)
  case class JwtConfig(
      publicKey: String
  )

}
