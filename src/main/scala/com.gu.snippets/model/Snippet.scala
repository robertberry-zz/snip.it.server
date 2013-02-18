package com.gu.snippets.model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.record.field.{EnumNameField, StringField}
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.Extraction

/** */
class Snippet private() extends MongoRecord[Snippet] with ObjectIdPk[Snippet] {
  def meta = Snippet

  object email extends StringField(this, 255)
  object contentType extends EnumNameField(this, ContentType)
  object content extends StringField(this, 1500)
  object articleID extends StringField(this, 200)
  object reference extends StringField(this, 200)
}

object Snippet extends Snippet with MongoMetaRecord[Snippet] {
  override def collectionName = "snippets"
  override def mongoIdentifier = SnippetMongo

  implicit def toJson(snippets: List[Snippet]): JValue = Extraction.decompose(snippets)
  implicit def toJson(snippet: Snippet): JValue = snippet.asJValue
}