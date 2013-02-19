package com.gu.snippets.model

import com.foursquare.rogue.LiftRogue._
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.{DateTimeField, EnumNameField, StringField}
import org.joda.time.DateTime
import java.util.{Locale, Calendar}

/** Action of saving, etc. a Snippet */
class Action private() extends MongoRecord[Action] with ObjectIdPk[Action] {
  def meta = Action

  object email extends StringField(this, 255)
  object username extends StringField(this, 100)
  object articleID extends StringField(this, 200)
  object reference extends StringField(this, 200)
  object actionType extends EnumNameField(this, ActionType)
  object created extends DateTimeField(this)
}

object Action extends Action with MongoMetaRecord[Action] {
  override def collectionName = "actions"
  override def mongoIdentifier = SnippetMongo

  def forSnippet(snippet: Snippet): List[Action] = {
    val articleID = snippet.articleID.get
    val reference = snippet.reference.get

    Action where (_.articleID eqs articleID) and (_.reference eqs reference) fetch()
  }

  private def create(articleID: String, reference: String, email: String, username: String) =
    Action.createRecord.articleID(articleID).reference(reference).email(email).created(
      DateTime.now().toCalendar(Locale.UK))

  def share(articleID: String, reference: String, email: String, username: String): Action =
    create(articleID, reference, email, username).actionType(ActionType.share).save

  def share(snippet: Snippet): Action =
    share(snippet.articleID.get, snippet.reference.get, snippet.email.get, snippet.username.get)

  def comment(articleID: String, reference: String, email: String, username: String): Action =
    create(articleID, reference, email, username).actionType(ActionType.comment).save

  def comment(snippet: Snippet): Action =
    comment(snippet.articleID.get, snippet.reference.get, snippet.email.get, snippet.username.get)

  def embed(articleID: String, reference: String, email: String, username: String): Action =
    create(articleID, reference, email, username).actionType(ActionType.embed).save

  def embed(snippet: Snippet): Action =
    embed(snippet.articleID.get, snippet.reference.get, snippet.email.get, snippet.username.get)

  def keep(articleID: String, reference: String, email: String, username: String): Action =
    create(articleID, reference, email, username).actionType(ActionType.save).save

  def keep(snippet: Snippet): Action =
    keep(snippet.articleID.get, snippet.reference.get, snippet.email.get, snippet.username.get)

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