package com.wanari.tutelar.example.akka.utils

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, onComplete}
import akka.http.scaladsl.server.Route
import cats.data.EitherT
import org.slf4j.Logger
import spray.json.{JsObject, RootJsonFormat, RootJsonWriter}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object Errors {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  type ErrorOr[F[_], T] = EitherT[F, AppError, T]
  type ErrorHandler     = PartialFunction[AppError, Route]

  sealed trait AppError {
    def message: String
  }

  case class GeneralError(message: String) extends AppError

  case class NotFoundError() extends AppError {
    override def message: String = "The requested entity was not found"
  }

  implicit class DbResponseWrapper[T](val response: Future[T]) {
    import cats.implicits._
    def toErrorOr(implicit executionContext: ExecutionContext): ErrorOr[Future, T] = {
      response.attemptT.leftMap(err => GeneralError(err.getMessage))
    }
  }

  implicit class DbOPResponseWrapper(val response: Future[Int]) {
    import cats.implicits._
    def toErrorOr(implicit executionContext: ExecutionContext): ErrorOr[Future, Unit] = {
      response.attemptT.leftMap(err => GeneralError(err.getMessage)).ensure(NotFoundError(): AppError)(_ == 1).map(_ => {})
    }
  }

  implicit class ResponseWrapper[T](val response: ErrorOr[Future, T]) {
    def toComplete(implicit w: RootJsonWriter[T], logger: Logger): Route = {
      toComplete(None)
    }
    def toComplete(handler: ErrorHandler)(implicit w: RootJsonWriter[T], logger: Logger): Route = {
      toComplete(Option(handler))
    }

    private def toComplete(
        mbHandler: Option[ErrorHandler]
    )(implicit w: RootJsonWriter[T], logger: Logger) = {
      val defaultHandler: ErrorHandler = {
        case notFound: NotFoundError =>
          complete((StatusCodes.NotFound, ErrorResponse(notFound.message)))
        case appError: AppError =>
          logger.info(appError.message)
          complete((StatusCodes.InternalServerError, ErrorResponse(appError.message)))
      }
      val errorHandler = mbHandler.map(_.orElse(defaultHandler)).getOrElse(defaultHandler)

      onComplete(response.value) {
        case Success(Right(res))  => complete(res)
        case Success(Left(error)) => errorHandler(error)
        case Failure(error) =>
          logger.error("Unhandled error!", error)
          complete(StatusCodes.InternalServerError)
      }
    }
  }

  case class ErrorResponse(error: String)

  import spray.json.DefaultJsonProtocol._
  implicit val errorResponseFormat: RootJsonFormat[ErrorResponse] = jsonFormat1(ErrorResponse.apply)
  implicit val unitWriter: RootJsonWriter[Unit]                   = (_: Unit) => JsObject()
}
