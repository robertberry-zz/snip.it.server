package com.gu.snippets.snippet


import net.liftweb.http.rest.RestHelper
import com.gu.snippets.model.{Snippet, Action}
import net.liftweb.json.Extraction
import com.foursquare.rogue.LiftRogue._
import net.liftweb.common.{Full, Box, Loggable}
import net.liftweb.json.JsonAST.JValue
import net.liftweb.http.{JsonResponse, OkResponse, LiftResponse}

/** blah */
object SnippetApi extends RestHelper with Loggable {
  def snippetsAsJson(snippets: List[Snippet]) = Extraction.decompose(snippets.map(_.asJValue))

  implicit def action2LiftResponse(action: Action) = JsonResponse(action.asJValue)

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
    case Nil JsonGet _ => snippetsAsJson(Snippet.findAll)

    case "save" :: Nil JsonPost jSnippet -> _ => {
      val snippet = ensureExists(jSnippet)

      snippet.map { snippet => Action.keep(snippet) }
    }

    case "share" :: Nil JsonPost jSnippet -> _ => {
      val snippet = ensureExists(jSnippet)

      snippet.map { snippet => Action.share(snippet) }
    }

    case "embed" :: Nil JsonPost jSnippet -> _ => {
      val snippet = ensureExists(jSnippet)

      snippet.map { snippet => Action.embed(snippet) }
    }

    case "comment" :: Nil JsonPost jSnippet -> _ => {
      val snippet = ensureExists(jSnippet)

      snippet.map { snippet => Action.comment(snippet) }
    }

    case "article" :: articleID JsonGet _ => {
      val aID = "/" + recomposeUrl(articleID)

      snippetsAsJson(Snippet where (_.articleID eqs aID) fetch())
    }
  })
}
