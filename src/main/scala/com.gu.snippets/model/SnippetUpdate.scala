package com.gu.snippets.model

import org.joda.time.DateTime

/** Nicer JSON format for the snippet updates */
case class SnippetUpdate(
    id: String,
    articleID: String,
    reference: String,
    contentType: String,
    content: String,
    email: String,
    username: String,
    action: String,
    create: String)

object SnippetUpdate {
  def apply(snippet: Snippet, action: Action): SnippetUpdate = {
    new SnippetUpdate(
      snippet.id.get.toString,
      snippet.articleID.get,
      snippet.reference.get,
      snippet.contentType.get.toString,
      snippet.content.get,
      action.email.get,
      action.username.get,
      action.actionType.get.toString,
      new DateTime(action.created.get).toString
    )
  }
}
