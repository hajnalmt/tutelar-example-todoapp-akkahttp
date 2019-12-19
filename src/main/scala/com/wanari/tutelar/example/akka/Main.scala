package com.wanari.tutelar.example.akka

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import com.wanari.tutelar.example.akka.utils._
import org.slf4j.{Logger, LoggerFactory}
import akka.http.scaladsl.model.HttpMethods.DELETE
import akka.http.scaladsl.server.Route
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.wanari.tutelar.example.akka.api.{HealthCheckApi, TodoApi}
import com.wanari.tutelar.example.akka.repo.TodoRepository

import scala.concurrent.ExecutionContext

object Main extends App {
  LogBridge.initLogBridge()

  implicit val system: ActorSystem          = ActorSystem("tutelar-ex-todo-akkahttp")
  implicit val dispatcher: ExecutionContext = system.dispatcher

  implicit val logger: Logger = LoggerFactory.getLogger(classOf[App])

  val config = new Config()
  val db     = SlickDb.createDb(config.db)

  val todoRepository = new TodoRepository(db)

  val healthCheckApi = new HealthCheckApi()
  val todoApi = new TodoApi(todoRepository, config.jwtConfig)


  val defaultHttpMethods = CorsSettings.defaultSettings.allowedMethods
  val corsSettings       = CorsSettings.defaultSettings.withAllowedMethods(defaultHttpMethods ++ Seq(DELETE))

  val routes: Route = {
    import akka.http.scaladsl.server.Directives._
    cors(corsSettings) {
      pathPrefix("api") {
        healthCheckApi.routes ~
        todoApi.routes
      }
    }
  }

  (for {
    _ <- Migrator.run(config.db)
    _ <- AkkaWebserver.startWebserver(routes)
  } yield ()).recoverWith { case _ => system.terminate.map(_ => System.exit(2)) }


}
