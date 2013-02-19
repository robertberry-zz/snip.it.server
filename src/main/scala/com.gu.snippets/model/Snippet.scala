package com.gu.snippets.model

import com.foursquare.rogue.LiftRogue._
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.record.field.{LongField, EnumNameField, StringField}
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.common.Box

/** */
class Snippet private() extends MongoRecord[Snippet] with ObjectIdPk[Snippet] {
  def meta = Snippet

  object contentType extends EnumNameField(this, ContentType)
  object content extends StringField(this, 1500)
  object articleID extends StringField(this, 200)
  object reference extends StringField(this, 200)

  // e-mail of who originally saved the snippet
  object email extends StringField(this, 255)
  object username extends StringField(this, 100)

  /* counts */
  object saves extends LongField(this)
  object shares extends LongField(this)
  object embeds extends LongField(this)
  object comments extends LongField(this)
}

object Snippet extends Snippet with MongoMetaRecord[Snippet] {
  override def collectionName = "snippets"
  override def mongoIdentifier = SnippetMongo

  def apply(articleID: String, reference: String): Box[Snippet] = {
    (Snippet where (_.articleID eqs articleID) and (_.reference eqs reference ) fetch()).headOption
  }

  def unapply(articleID: String, reference: String): Box[Snippet] = apply(articleID, reference)

  def forArticle(articleID: String): List[Snippet] = {
    Snippet where (_.articleID eqs articleID) fetch()
  }

  def isCreated_?(snippet: Snippet) = apply(snippet.articleID.get, snippet.reference.get).isDefined
}