package com.wanari.tutelar.example.akka.utils

import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import slick.jdbc.PostgresProfile

object SlickDb {

  def createDb(conf: Config.Database): PostgresProfile.backend.Database = {
    import slick.jdbc.PostgresProfile.api._
    val customDbConf = ConfigFactory
      .load()
      .withValue("psqldb.registerMbeans", ConfigValueFactory.fromAnyRef(true))
      .withValue("psqldb.properties.url", ConfigValueFactory.fromAnyRef(conf.url))
      .withValue("psqldb.properties.user", ConfigValueFactory.fromAnyRef(conf.user))
      .withValue("psqldb.properties.password", ConfigValueFactory.fromAnyRef(conf.password))
      .withValue("psqldb.properties.driver", ConfigValueFactory.fromAnyRef("slick.jdbc.PostgresProfile"))
      .withValue("psqldb.dataSourceClass", ConfigValueFactory.fromAnyRef("slick.jdbc.DatabaseUrlDataSource"))

    Database.forConfig("psqldb", customDbConf)
  }

}
