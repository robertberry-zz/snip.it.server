package com.gu.snippets.model

import net.liftweb.mongodb.{MongoDB, MongoIdentifier}
import net.liftweb.util.Helpers.tryo
import net.liftweb.util.Props
import net.liftweb.common.{Loggable, Full, Box, Empty}
import com.mongodb.{ServerAddress, Mongo}

/** Connection for the Mongo instance */
object SnippetMongo extends MongoIdentifier with Loggable {
  val DefaultPort = 37648
  val DefaultServer = "localhost"
  val DefaultName = "snippets"
  val DefaultUser = ""
  val DefaultPassword = ""

  override def jndiName = Props.get("mongo.name").getOrElse(DefaultName)

  private var mongo: Box[Mongo] = Empty

  def connectToMongo() {
    val port = (for {
      portString <- Props.get("mongo.port")
      port <- tryo { portString.toInt }
    } yield port).getOrElse(DefaultPort)

    val server = Props.get("mongo.server").getOrElse(DefaultServer)
    val name = Props.get("mongo.name").getOrElse(DefaultName)
    val user = Props.get("mongo.user").getOrElse(DefaultUser)
    val password = Props.get("mongo.password").getOrElse(DefaultPassword)

    logger.warn("Connecting to Mongo: server=%s name=%s user=%s password=%s".format(server, name, user, password))

    mongo = Full(new Mongo(new ServerAddress(server, port)))
    MongoDB.defineDbAuth(SnippetMongo, mongo.get, name, user, password)
  }

  def disconnectFromMongo() {
    mongo.foreach(_.close())

    logger.info("Disconnecting from Mongo")

    MongoDB.close
    mongo = Empty
  }
}
