package com.gu.snippets.model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.{EnumNameField, StringField}

/** Action of saving, etc. a Snippet */
class Action private() extends MongoRecord[Action] with ObjectIdPk[Action] {
  def meta = Action

  object articleID extends StringField(this, 200)
  object reference extends StringField(this, 200)
  object actionType extends EnumNameField(this, ActionType)
}

object Action extends Action with MongoMetaRecord[Action] {
  override def collectionName = "actions"
  override def mongoIdentifier = SnippetMongo

  private def create(articleID: String, reference: String) =
    Action.createRecord.articleID(articleID).reference(reference)

  def share(articleID: String, reference: String): Action =
    create(articleID, reference).actionType(ActionType.share).save

  def share(snippet: Snippet): Action =
    share(snippet.articleID.get, snippet.reference.get)

  def comment(articleID: String, reference: String): Action =
    create(articleID, reference).actionType(ActionType.comment).save

  def comment(snippet: Snippet): Action =
    comment(snippet.articleID.get, snippet.reference.get)

  def embed(articleID: String, reference: String): Action =
    create(articleID, reference).actionType(ActionType.embed).save

  def embed(snippet: Snippet): Action =
    embed(snippet.articleID.get, snippet.reference.get)

  def keep(articleID: String, reference: String): Action =
    create(articleID, reference).actionType(ActionType.save).save

  def keep(snippet: Snippet): Action =
    keep(snippet.articleID.get, snippet.reference.get)
}