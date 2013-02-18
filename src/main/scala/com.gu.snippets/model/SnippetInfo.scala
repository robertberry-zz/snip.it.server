package com.gu.snippets.model

import org.joda.time.DateTime

/* nicer output format for API */

case class ActionPack(
    saves: List[ActionInfo],
    embeds: List[ActionInfo],
    comments: List[ActionInfo],
    shares: List[ActionInfo])

object ActionPack {
  def apply(actions: List[Action]): ActionPack = {
    val byType = actions groupBy { _.actionType.get } mapValues { _.map(ActionInfo(_)) }

    new ActionPack(
      byType.getOrElse(ActionType.save, Nil),
      byType.getOrElse(ActionType.embed, Nil),
      byType.getOrElse(ActionType.comment, Nil),
      byType.getOrElse(ActionType.share, Nil)
    )
  }
}

class ActionInfo(
    val email: String,
    val dateTime: String)

object ActionInfo {
  def apply(action: Action): ActionInfo = {

    new ActionInfo(action.email.get, new DateTime(action.created.get).toString)
  }
}

/** SnippetInfo */
class SnippetInfo(
    val articleID: String,
    val reference: String,
    val contentType: String,
    val content: String,
    val saves: Long,
    val shares: Long,
    val embeds: Long,
    val comments: Long,
    val actions: ActionPack)

object SnippetInfo {
  def apply(snippet: Snippet): SnippetInfo = {
    val actions = ActionPack(Action.forSnippet(snippet))

    new SnippetInfo(
        snippet.articleID.get,
        snippet.reference.get,
        snippet.contentType.get.toString,
        snippet.content.get,
        actions.saves.length,
        actions.shares.length,
        actions.embeds.length,
        actions.comments.length,
        actions
    )
  }
}