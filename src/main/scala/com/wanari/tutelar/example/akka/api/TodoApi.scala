package com.wanari.tutelar.example.akka.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.LongNumber
import com.wanari.tutelar.example.akka.api.HealthCheckApi.HealthCheckDto
import com.wanari.tutelar.example.akka.api.TodoApi.{TodoDto, TodoListDto}
import com.wanari.tutelar.example.akka.repo.TodoRepository
import com.wanari.tutelar.example.akka.repo.TodoRepository.TodoDbo
import com.wanari.tutelar.example.akka.utils.Config.JwtConfig
import org.slf4j.{Logger, LoggerFactory}
import spray.json.RootJsonFormat

import scala.concurrent.ExecutionContext

class TodoApi(
  todoRepository: TodoRepository,
  val jwtConfig: JwtConfig
)(
  implicit executionContext: ExecutionContext
) extends Authentication {

  implicit val logger: Logger = LoggerFactory.getLogger(classOf[TodoApi])

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import com.wanari.tutelar.example.akka.utils.Errors._

  val routes = list ~ insert ~ update ~ delete

  def list = (get & path("todo")) {
    authenticatedWithPayload { user =>
      todoRepository.listByUser(user.id).map(TodoListDto.fromDbo).toErrorOr.toComplete
    }
  }

  def insert = (post & path("todo")) {
    authenticatedWithPayload { user =>
        entity(as[TodoDto]) { dto =>
          todoRepository.insert(user.id, dto.title, dto.done).toErrorOr.toComplete
        }
      }
    }

  def update = (post & path("todo" / LongNumber)) { id =>
    authenticatedWithPayload { user =>
        entity(as[TodoDto]) { dto =>
          todoRepository.update(user.id, dto.title, dto.done, id).toErrorOr.toComplete
        }
      }
    }


  def delete = (post & path("todo" / LongNumber / "delete")) { id =>
    authenticatedWithPayload { user =>
      todoRepository.delete(user.id, id).toErrorOr.toComplete
    }
  }

}

object TodoApi {
  import spray.json.DefaultJsonProtocol._
  implicit val todoDtoFormat: RootJsonFormat[TodoDto] = jsonFormat2(TodoDto)
  implicit val savedTodoDtoFormat: RootJsonFormat[SavedTodoDto] = jsonFormat3(SavedTodoDto)
  implicit val todoListDtoFormat: RootJsonFormat[TodoListDto] = jsonFormat1(TodoListDto.apply)

  case class TodoDto(title: String, done: Boolean)
  case class SavedTodoDto(id: Long, title: String, done: Boolean)
  case class TodoListDto(todos: Seq[SavedTodoDto])
  object TodoListDto {
    def fromDbo(todos: Seq[TodoDbo]): TodoListDto = {
      TodoListDto(todos.map(t => SavedTodoDto(t.id, t.title, t.done)))
    }
  }
}
