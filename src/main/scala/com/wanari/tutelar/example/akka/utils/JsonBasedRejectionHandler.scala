package com.wanari.tutelar.example.akka.utils

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.RejectionHandler
import com.wanari.tutelar.example.akka.utils.Errors._
import spray.json._

/**
  * mostly based on
  * https://doc.akka.io/docs/akka-http/current/routing-dsl/rejections.html#customising-rejection-http-responses
  */
object JsonBasedRejectionHandler {

  implicit def jsonRejectionHandler: RejectionHandler =
    RejectionHandler.default
      .mapRejectionResponse {
        case res @ HttpResponse(_, _, ent: HttpEntity.Strict, _) =>
          // since all Akka default rejection responses are Strict this will handle all rejections
          val message = ent.data.utf8String
          res.copy(entity = HttpEntity(ContentTypes.`application/json`, ErrorResponse(message).toJson.toString))
        case x => x // pass through all other types of responses
      }
}
