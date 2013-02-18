package com.gu.snippets.model

/** Types of content a snippet can represent */
object ContentType extends Enumeration {
  val text = Value("text")
  val video = Value("video")
  val image = Value("image")
}
