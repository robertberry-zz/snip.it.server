package com.gu.snippets.model

import com.foursquare.rogue.LiftRogue._
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.{DateTimeField, EnumNameField, StringField}
import org.joda.time.DateTime
import java.util.{Locale}

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

  private var onCreateCallbacks = List[(Action) => Unit]()

  def onCreate(callback: (Action) => Unit) {
    synchronized {
      onCreateCallbacks = callback :: onCreateCallbacks
    }
  }

  def offCreate(callback: (Action) => Unit) {
    synchronized {
      onCreateCallbacks = onCreateCallbacks filterNot { _ == callback }
    }
  }

  def forSnippet(snippet: Snippet): List[Action] = {
    val articleID = snippet.articleID.get
    val reference = snippet.reference.get

    Action where (_.articleID eqs articleID) and (_.reference eqs reference) fetch()
  }

  def apply(snippet: Snippet, actionType: ActionType.Value): Action = {
    val action = Action.createRecord.articleID(snippet.articleID.get)
      .reference(snippet.reference.get)
      .email(snippet.email.get)
      .created(DateTime.now().toCalendar(Locale.UK))
      .actionType(actionType)
      .save

    for (callback <- onCreateCallbacks) {
      callback(action)
    }

    action
  }

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