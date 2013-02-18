package com.gu.snippets.snippet


import net.liftweb.http.rest.RestHelper
import com.gu.snippets.model.{Snippet, Action}
import net.liftweb.json.Extraction
import com.foursquare.rogue.LiftRogue._
import net.liftweb.common.{Box, Loggable}
import net.liftweb.json.JsonAST.JValue
import net.liftweb.http.{JsonResponse}

/** blah */
object SnippetApi extends RestHelper with Loggable {
  def snippetsAsJson(snippets: List[Snippet]) = Extraction.decompose(snippets.map(_.asJValue))

  implicit def snippets2LiftResponse(snippets: List[Snippet]) = JsonResponse(snippetsAsJson(snippets))
  implicit def action2LiftResponse(action: Action) = JsonResponse(action.asJValue)
  implicit def snippet2LiftResponse(snippet: Snippet) = JsonResponse(snippet.asJValue)

  def recomposeUrl(parts: List[String]) = parts.reduceLeft { _ + "/" + _ }

  def ensureExists(jSnippet: JValue): Box[Snippet] = {
    // save snippet if not already in DB
    for {
      snippet <- Snippet.fromJValue(jSnippet)
    } yield {
      if (!Snippet.isCreated_?(snippet)) {
        snippet.save
      }
      snippet
    }
  }

  serve("api" / "snippet" prefix {
    case Nil JsonGet _ => Snippet.findAll

    case "save" :: Nil JsonPost json -> _ => {
      val snippet = ensureExists(json)

      for (snippet <- snippet) yield {
        val previouslyKept = Action.kept_?(snippet)

        previouslyKept.getOrElse({
          snippet.saves.atomicUpdate(_ + 1)
          Action.keep(snippet)
        })
      }
    }

    case "share" :: Nil JsonPost jSnippet -> _ => {
      val snippet = ensureExists(jSnippet)

      snippet.foreach(_.shares.atomicUpdate(_ + 1))

      snippet.map { snippet => Action.share(snippet) }
    }

    case "embed" :: Nil JsonPost jSnippet -> _ => {
      val snippet = ensureExists(jSnippet)

      snippet.foreach(_.embeds.atomicUpdate(_ + 1))

      snippet.map { snippet => Action.embed(snippet) }
    }

    case "comment" :: Nil JsonPost jSnippet -> _ => {
      val snippet = ensureExists(jSnippet)

      snippet.foreach(_.comments.atomicUpdate(_ + 1))

      snippet.map { snippet => Action.comment(snippet) }
    }

    case "article" :: articleID JsonGet _ => {
      Snippet where (_.articleID eqs "/" + recomposeUrl(articleID)) fetch()
    }
  })
}
