package com.gu.snippets.model

import org.junit.{Before, After}
import org.specs2.mutable._

class SnippetSpec extends Specification {
  sequential

  @Before
  def setupMongo() {
    SnippetMongo.connectToMongo()
  }

  @After
  def tearDownMongo() {
    SnippetMongo.disconnectFromMongo()
  }

  "Snippet" should {
    "be able to create and delete records" in {
      setupMongo()

      val snippet = Snippet.createRecord
        .articleID("/society/2013/feb/18/doctors-soft-drinks-tax-obesity")
        .contentType(ContentType.text)
        .content("The academy wants a dramatic increase in anti-obesity efforts.")
        .email("robert.berry@guardian.co.uk")
        .reference("?")
        .save

      snippet.delete_! must beTrue

      tearDownMongo()
    }
  }

  "Snippet" should {
    "read records" in {
      setupMongo()

      val snippets = Snippet.findAll

      snippets.length must beEqualTo(1)

      tearDownMongo()
    }
  }
}
