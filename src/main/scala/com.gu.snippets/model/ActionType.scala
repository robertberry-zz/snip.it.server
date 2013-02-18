package com.gu.snippets.model

/** Different types of actions associated with snippets */
object ActionType extends Enumeration {
  val save = Value("save")
  val share = Value("share")
  val comment = Value("comment")
  val embed = Value("embed")
}
