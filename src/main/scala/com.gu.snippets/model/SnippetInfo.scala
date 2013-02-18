package com.gu.snippets.model

/* nicer output format for API */

class ActionPack(
    val saves: List[ActionInfo],
    val embeds: List[ActionInfo],
    val comments: List[ActionInfo],
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
    val email: String)

object ActionInfo {
  def apply(action: Action): ActionInfo = {
    new ActionInfo(action.email.get)
  }
}

/** SnippetInfo */
class SnippetInfo(
    val articleID: String,
    val reference: String,
    val contentType: ContentType.Value,
    val content: String,
    val saves: Long,
    val shares: Long,
    val embeds: Long,
    val comments: Long,
    val actions: ActionPack)

object SnippetInfo {
  def apply(snippet: Snippet): SnippetInfo = {
    val actions = Action.forSnippet(snippet)

    new SnippetInfo(
        snippet.articleID.get,
        snippet.reference.get,
        snippet.contentType.get,
        snippet.content.get,
        snippet.saves.get,
        snippet.shares.get,
        snippet.embeds.get,
        snippet.comments.get,
        ActionPack(actions)
    )
  }
}