package com.gu.snippets.snippet


import net.liftweb.http.rest.RestHelper
import com.gu.snippets.model.Snippet
import net.liftweb.json.Extraction
import com.foursquare.rogue.LiftRogue._
import net.liftweb.common.Loggable

/** blah */
object SnippetApi extends RestHelper with Loggable {
  def snippetsAsJson(snippets: List[Snippet]) = Extraction.decompose(snippets.map(_.asJValue))

  def recomposeUrl(parts: List[String]) = parts.reduceLeft { _ + "/" + _ }

  serve("api" / "snippet" prefix {
    case Nil JsonGet _ => snippetsAsJson(Snippet.findAll)

    case Nil JsonPost snippet -> _ => Snippet.fromJValue(snippet).map(_.save).map(_.asJValue)

    case "article" :: articleID JsonGet _ => {
      val aID = "/" + recomposeUrl(articleID)

      logger.info(aID)

      snippetsAsJson(Snippet where (_.articleID eqs aID) fetch())
    }
  })
}
