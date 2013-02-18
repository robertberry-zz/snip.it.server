package com.gu.snippets.model

import net.liftweb.common.Box

/**
 * Created with IntelliJ IDEA.
 * User: robert
 * Date: 18/02/13
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
class SnippetInfo(val snippet: Snippet, val saves: Int, val shares: Int, val embeds: Int, val comments: Int)

object SnippetInfo {
  def apply(articleID: String, reference: String): Box[SnippetInfo] = {
    null
  }
}
