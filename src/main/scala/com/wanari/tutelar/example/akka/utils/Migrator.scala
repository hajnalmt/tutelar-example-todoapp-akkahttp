package com.wanari.tutelar.example.akka.utils

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object Migrator {
  val logger: Logger = LoggerFactory.getLogger("Migrator")

  def run(db: Config.Database)(implicit executionContext: ExecutionContext): Future[Unit] = {
    val url  = db.url
    val user = db.user
    val pass = db.password
    run(url, user, pass, db.forceMigration)
  }

  def run(url: String, user: String, pass: String, forceMigration: Boolean)(implicit executionContext: ExecutionContext): Future[Unit] = {
    val res = Future {
      val config: FluentConfiguration = new FluentConfiguration()
        .table("schema_version")
        .baselineVersion("0.0")
        .baselineOnMigrate(forceMigration)
        .dataSource(url, user, pass)
      val flyway: Flyway = new Flyway(config)
      flyway.migrate()
    }
    res.onComplete {
      case Failure(ex) => logger.error("Migration failed", ex)
      case Success(_)  => logger.info("Migration completed")
    }
    res.map(_ => {})
  }
}
