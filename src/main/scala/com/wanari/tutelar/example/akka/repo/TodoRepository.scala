package com.wanari.tutelar.example.akka.repo

import com.wanari.tutelar.example.akka.repo.TodoRepository.TodoDbo

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._

class TodoRepository(
  db: Database
)(
  implicit executionContext: ExecutionContext
) {

  def listByUser(userId: String): Future[Seq[TodoDbo]] = {
    db.run(todos.filter(_.userId === userId).result)
  }

  def insert(userId: String, title: String, done: Boolean): Future[Int] = {
    db.run(todos += TodoDbo(userId, title, done))
  }

  def update(userId: String, title: String, done: Boolean, id: Long): Future[Int] = {
    db.run(todos.filter(_.id === id).filter(_.userId === userId).map(r => (r.title, r.done)).update((title, done)))
  }

  def delete(userId: String, id: Long): Future[Int] = {
    db.run(todos.filter(_.id === id).filter(_.userId === userId).delete)
  }

  val todos = TableQuery[Todos]

  class Todos(tag: Tag) extends Table[TodoDbo](tag, "todos") {
    def id         = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userId   = column[String]("user_id")
    def title       = column[String]("title")
    def done     = column[Boolean]("done")

    def * =
      (userId, title, done, id) <> (TodoDbo.tupled, TodoDbo.unapply)
  }
}

object TodoRepository {

  case class TodoDbo(userId: String, title: String, done: Boolean, id: Long = 0)

}
