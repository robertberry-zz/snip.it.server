package com.gu.snippets.model

import com.foursquare.rogue.LiftRogue._
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.record.field.{EnumNameField, StringField}
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.common.Box

/** */
class Snippet private() extends MongoRecord[Snippet] with ObjectIdPk[Snippet] {
  def meta = Snippet

  object contentType extends EnumNameField(this, ContentType)
  object content extends StringField(this, 1500)
  object articleID extends StringField(this, 200)
  object reference extends StringField(this, 200)
}

object Snippet extends Snippet with MongoMetaRecord[Snippet] {
  override def collectionName = "snippets"
  override def mongoIdentifier = SnippetMongo

  def apply(articleID: String, reference: String): Box[Snippet] = {
    (Snippet where (_.articleID eqs articleID) and (_.reference eqs reference ) fetch()).headOption
  }

  def unapply(articleID: String, reference: String): Box[Snippet] = apply(articleID, reference)

  def isCreated_?(snippet: Snippet) = apply(snippet.articleID.get, snippet.reference.get).isDefined
}