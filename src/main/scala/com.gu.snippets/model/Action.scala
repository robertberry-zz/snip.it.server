package com.gu.snippets.model

import com.foursquare.rogue.LiftRogue._
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.{EnumNameField, StringField}

/** Action of saving, etc. a Snippet */
class Action private() extends MongoRecord[Action] with ObjectIdPk[Action] {
  def meta = Action

  object email extends StringField(this, 255)
  object articleID extends StringField(this, 200)
  object reference extends StringField(this, 200)
  object actionType extends EnumNameField(this, ActionType)
}

object Action extends Action with MongoMetaRecord[Action] {
  override def collectionName = "actions"
  override def mongoIdentifier = SnippetMongo

  private def create(articleID: String, reference: String, email: String) =
    Action.createRecord.articleID(articleID).reference(reference).email(email)

  def share(articleID: String, reference: String, email: String): Action =
    create(articleID, reference, email).actionType(ActionType.share).save

  def share(snippet: Snippet): Action =
    share(snippet.articleID.get, snippet.reference.get, snippet.email.get)

  def comment(articleID: String, reference: String, email: String): Action =
    create(articleID, reference, email).actionType(ActionType.comment).save

  def comment(snippet: Snippet): Action =
    comment(snippet.articleID.get, snippet.reference.get, snippet.email.get)

  def embed(articleID: String, reference: String, email: String): Action =
    create(articleID, reference, email).actionType(ActionType.embed).save

  def embed(snippet: Snippet): Action =
    embed(snippet.articleID.get, snippet.reference.get, snippet.email.get)

  def keep(articleID: String, reference: String, email: String): Action =
    create(articleID, reference, email).actionType(ActionType.save).save

  def keep(snippet: Snippet): Action =
    keep(snippet.articleID.get, snippet.reference.get, snippet.email.get)

  // has the snippet already been kept? if so return original action
  // TODO this is crappy 'cos it relies on the e-mail field in Snippet, which should really be the creator's
  def kept_?(snippet: Snippet): Option[Action] = {
    val articleID = snippet.articleID.get
    val reference = snippet.reference.get
    val email = snippet.email.get

    (Action where (_.articleID eqs articleID) and
      (_.reference eqs reference) and
      (_.email eqs email) fetch()).headOption
  }
}