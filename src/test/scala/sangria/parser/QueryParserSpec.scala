package sangria.parser

import language.postfixOps
import org.parboiled2.Position
import org.scalatest.{Matchers, WordSpec}
import sangria.ast._
import sangria.util.{FileUtil, StringMatchers}

import scala.reflect.ClassTag
import scala.util.{Failure, Success}

class QueryParserSpec extends WordSpec with Matchers with StringMatchers {

  "QueryParser" should {
    "parse complex query" in {
      val query = FileUtil loadQuery "complex-query.graphql"

      val expectedAst =
        Document(
          Vector(
            OperationDefinition(
              OperationType.Query,
              Some("FetchLukeAndLeiaAliased"),
              Vector(
                VariableDefinition(
                  "someVar",
                  NamedType("Int", Some(Position(53, 2, 41))),
                  Some(BigDecimalValue(1.23, Vector.empty, Some(Position(59, 2, 47)))),
                  Vector.empty,
                  Some(Position(43, 2, 31))
                ),
                VariableDefinition(
                  "anotherVar",
                  NamedType("Int", Some(Position(77, 2, 65))),
                  Some(BigIntValue(123, Vector.empty, Some(Position(83, 2, 71)))),
                  Vector.empty,
                  Some(Position(64, 2, 52))
                )),
              Vector(
                Directive(
                  "include",
                  Vector(
                    Argument(
                      "if",
                      BooleanValue(true, Vector.empty, Some(Position(100, 2, 88))),
                      Vector.empty,
                      Some(Position(96, 2, 84))
                    )),
                  Vector.empty,
                  Some(Position(87, 2, 75))
                ),
                Directive(
                  "include",
                  Vector(
                    Argument(
                      "if",
                      BooleanValue(false, Vector.empty, Some(Position(119, 2, 107))),
                      Vector.empty,
                      Some(Position(115, 2, 103))
                    )),
                  Vector.empty,
                  Some(Position(106, 2, 94))
                )),
              Vector(
                Field(
                  Some("luke"),
                  "human",
                  Vector(
                    Argument(
                      "id",
                      StringValue("1000", Vector.empty, Some(Position(145, 3, 19))),
                      Vector.empty,
                      Some(Position(141, 3, 15))
                    )),
                  Vector(
                    Directive(
                      "include",
                      Vector(
                        Argument(
                          "if",
                          BooleanValue(true, Vector.empty, Some(Position(165, 3, 39))),
                          Vector.empty,
                          Some(Position(161, 3, 35))
                        )),
                      Vector.empty,
                      Some(Position(152, 3, 26))
                    )),
                  Vector(
                    Field(
                      None,
                      "friends",
                      Vector(
                        Argument(
                          "sort",
                          EnumValue("NAME", Vector.empty, Some(Position(190, 4, 19))),
                          Vector.empty,
                          Some(Position(184, 4, 13))
                        )),
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Some(Position(176, 4, 5))
                    )),
                  Vector.empty,
                  Vector.empty,
                  Some(Position(129, 3, 3))
                ),
                Field(
                  Some("leia"),
                  "human",
                  Vector(
                    Argument(
                      "id",
                      StringValue("10103\n \u00F6 \u00F6", Vector.empty, Some(Position(223, 6, 24))),
                      Vector.empty,
                      Some(Position(214, 6, 15))
                    )),
                  Vector.empty,
                  Vector(
                    Field(
                      None,
                      "name",
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Some(Position(249, 7, 5))
                    )),
                  Vector.empty,
                  Vector(
                    Comment(" some name", Some(Position(254, 7, 10)))),
                  Some(Position(202, 6, 3))
                ),
                InlineFragment(
                  Some(NamedType("User", Some(Position(280, 10, 10)))),
                  Vector.empty,
                  Vector(
                    Field(
                      None,
                      "birth",
                      Vector.empty,
                      Vector.empty,
                      Vector(
                        Field(
                          None,
                          "day",
                          Vector.empty,
                          Vector.empty,
                          Vector.empty,
                          Vector.empty,
                          Vector.empty,
                          Some(Position(297, 11, 11))
                        )),
                      Vector.empty,
                      Vector.empty,
                      Some(Position(291, 11, 5))
                    )),
                  Vector.empty,
                  Vector.empty,
                  Some(Position(273, 10, 3))
                ),
                FragmentSpread("Foo", Vector.empty, Vector.empty, Some(Position(309, 14, 3)))),
              Vector(
                Comment(" test query", Some(Position(0, 1, 1)))),
              Vector.empty,
              Some(Position(13, 2, 1))
            ),
            FragmentDefinition(
              "Foo",
              NamedType("User", Some(Position(335, 17, 17))),
              Vector(
                Directive(
                  "foo",
                  Vector(
                    Argument(
                      "bar",
                      BigIntValue(1, Vector.empty, Some(Position(350, 17, 32))),
                      Vector.empty,
                      Some(Position(345, 17, 27))
                    )),
                  Vector.empty,
                  Some(Position(340, 17, 22))
                )),
              Vector(
                Field(
                  None,
                  "baz",
                  Vector.empty,
                  Vector.empty,
                  Vector.empty,
                  Vector.empty,
                  Vector.empty,
                  Some(Position(356, 18, 3))
                )),
              Vector.empty,
              Vector(
                Comment(" field in fragment!", Some(Position(360, 18, 7)))),
              Some(Position(319, 17, 1))
            )),
          Vector.empty,
          Some(Position(0, 1, 1)),
          None
        )

      QueryParser.parse(query) map (_.withoutSourceMapper) should be (Success(expectedAst))
    }

    "parse kitchen sink" in {
      val query = FileUtil loadQuery "kitchen-sink.graphql"

      val expectedAst =
        Document(
          Vector(
            OperationDefinition(
              OperationType.Query,
              Some("queryName"),
              Vector(
                VariableDefinition(
                  "foo",
                  NamedType("ComplexType", Some(Position(310, 8, 23))),
                  None,
                  Vector.empty,
                  Some(Position(304, 8, 17))
                ),
                VariableDefinition(
                  "site",
                  NamedType("Site", Some(Position(330, 8, 43))),
                  Some(EnumValue("MOBILE", Vector.empty, Some(Position(337, 8, 50)))),
                  Vector.empty,
                  Some(Position(323, 8, 36))
                )),
              Vector.empty,
              Vector(
                Field(
                  Some("whoever123is"),
                  "node",
                  Vector(
                    Argument(
                      "id",
                      ListValue(
                        Vector(
                          BigIntValue(123, Vector.empty, Some(Position(373, 9, 27))),
                          BigIntValue(456, Vector.empty, Some(Position(378, 9, 32)))),
                        Vector.empty,
                        Some(Position(372, 9, 26))
                      ),
                      Vector.empty,
                      Some(Position(368, 9, 22))
                    )),
                  Vector.empty,
                  Vector(
                    Field(
                      None,
                      "id",
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Some(Position(390, 10, 5))
                    ),
                    InlineFragment(
                      Some(NamedType("User", Some(Position(406, 11, 12)))),
                      Vector(
                        Directive(
                          "defer",
                          Vector.empty,
                          Vector.empty,
                          Some(Position(411, 11, 17))
                        )),
                      Vector(
                        Field(
                          None,
                          "field2",
                          Vector.empty,
                          Vector.empty,
                          Vector(
                            Field(
                              None,
                              "id",
                              Vector.empty,
                              Vector.empty,
                              Vector.empty,
                              Vector.empty,
                              Vector.empty,
                              Some(Position(443, 13, 9))
                            ),
                            Field(
                              Some("alias"),
                              "field1",
                              Vector(
                                Argument(
                                  "first",
                                  BigIntValue(10, Vector.empty, Some(Position(476, 14, 29))),
                                  Vector.empty,
                                  Some(Position(470, 14, 23))
                                ),
                                Argument(
                                  "after",
                                  VariableValue("foo", Vector.empty, Some(Position(486, 14, 39))),
                                  Vector.empty,
                                  Some(Position(480, 14, 33))
                                )),
                              Vector(
                                Directive(
                                  "include",
                                  Vector(
                                    Argument(
                                      "if",
                                      VariableValue("foo", Vector.empty, Some(Position(506, 14, 59))),
                                      Vector.empty,
                                      Some(Position(502, 14, 55))
                                    )),
                                  Vector.empty,
                                  Some(Position(493, 14, 46))
                                )),
                              Vector(
                                Field(
                                  None,
                                  "id",
                                  Vector.empty,
                                  Vector.empty,
                                  Vector.empty,
                                  Vector.empty,
                                  Vector.empty,
                                  Some(Position(524, 15, 11))
                                ),
                                FragmentSpread("frag", Vector.empty, Vector.empty, Some(Position(538, 16, 11)))),
                              Vector.empty,
                              Vector.empty,
                              Some(Position(456, 14, 9))
                            )),
                          Vector.empty,
                          Vector.empty,
                          Some(Position(426, 12, 7))
                        )),
                      Vector.empty,
                      Vector.empty,
                      Some(Position(399, 11, 5))
                    )),
                  Vector.empty,
                  Vector.empty,
                  Some(Position(349, 9, 3))
                )),
              Vector(
                Comment(" Copyright (c) 2015, Facebook, Inc.", Some(Position(0, 1, 1))),
                Comment(" All rights reserved.", Some(Position(37, 2, 1))),
                Comment("", Some(Position(60, 3, 1))),
                Comment(" This source code is licensed under the BSD-style license found in the", Some(Position(62, 4, 1))),
                Comment(" LICENSE file in the root directory of this source tree. An additional grant", Some(Position(134, 5, 1))),
                Comment(" of patent rights can be found in the PATENTS file in the same directory.", Some(Position(212, 6, 1)))),
              Vector.empty,
              Some(Position(288, 8, 1))
            ),
            OperationDefinition(
              OperationType.Mutation,
              Some("likeStory"),
              Vector.empty,
              Vector.empty,
              Vector(
                Field(
                  None,
                  "like",
                  Vector(
                    Argument(
                      "story",
                      BigIntValue(123, Vector.empty, Some(Position(612, 24, 15))),
                      Vector.empty,
                      Some(Position(605, 24, 8))
                    )),
                  Vector(
                    Directive(
                      "defer",
                      Vector.empty,
                      Vector.empty,
                      Some(Position(617, 24, 20))
                    )),
                  Vector(
                    Field(
                      None,
                      "story",
                      Vector.empty,
                      Vector.empty,
                      Vector(
                        Field(
                          None,
                          "id",
                          Vector.empty,
                          Vector.empty,
                          Vector.empty,
                          Vector.empty,
                          Vector.empty,
                          Some(Position(644, 26, 7))
                        )),
                      Vector.empty,
                      Vector.empty,
                      Some(Position(630, 25, 5))
                    )),
                  Vector.empty,
                  Vector.empty,
                  Some(Position(600, 24, 3))
                )),
              Vector.empty,
              Vector.empty,
              Some(Position(577, 23, 1))
            ),
            OperationDefinition(
              OperationType.Subscription,
              Some("StoryLikeSubscription"),
              Vector(
                VariableDefinition(
                  "input",
                  NamedType("StoryLikeSubscribeInput", Some(Position(703, 31, 44))),
                  None,
                  Vector.empty,
                  Some(Position(695, 31, 36))
                )),
              Vector.empty,
              Vector(
                Field(
                  None,
                  "storyLikeSubscribe",
                  Vector(
                    Argument(
                      "input",
                      VariableValue("input", Vector.empty, Some(Position(758, 32, 29))),
                      Vector.empty,
                      Some(Position(751, 32, 22))
                    )),
                  Vector.empty,
                  Vector(
                    Field(
                      None,
                      "story",
                      Vector.empty,
                      Vector.empty,
                      Vector(
                        Field(
                          None,
                          "likers",
                          Vector.empty,
                          Vector.empty,
                          Vector(
                            Field(
                              None,
                              "count",
                              Vector.empty,
                              Vector.empty,
                              Vector.empty,
                              Vector.empty,
                              Vector.empty,
                              Some(Position(803, 35, 9))
                            )),
                          Vector.empty,
                          Vector.empty,
                          Some(Position(786, 34, 7))
                        ),
                        Field(
                          None,
                          "likeSentence",
                          Vector.empty,
                          Vector.empty,
                          Vector(
                            Field(
                              None,
                              "text",
                              Vector.empty,
                              Vector.empty,
                              Vector.empty,
                              Vector.empty,
                              Vector.empty,
                              Some(Position(846, 38, 9))
                            )),
                          Vector.empty,
                          Vector.empty,
                          Some(Position(823, 37, 7))
                        )),
                      Vector.empty,
                      Vector.empty,
                      Some(Position(772, 33, 5))
                    )),
                  Vector.empty,
                  Vector.empty,
                  Some(Position(732, 32, 3))
                )),
              Vector.empty,
              Vector.empty,
              Some(Position(660, 31, 1))
            ),
            FragmentDefinition(
              "frag",
              NamedType("Friend", Some(Position(889, 44, 18))),
              Vector.empty,
              Vector(
                Field(
                  None,
                  "foo",
                  Vector(
                    Argument(
                      "size",
                      VariableValue("size", Vector.empty, Some(Position(910, 45, 13))),
                      Vector.empty,
                      Some(Position(904, 45, 7))
                    ),
                    Argument(
                      "bar",
                      VariableValue("b", Vector.empty, Some(Position(922, 45, 25))),
                      Vector.empty,
                      Some(Position(917, 45, 20))
                    ),
                    Argument(
                      "obj",
                      ObjectValue(
                        Vector(
                          ObjectField(
                            "key",
                            StringValue("value", Vector.empty, Some(Position(937, 45, 40))),
                            Vector.empty,
                            Some(Position(932, 45, 35))
                          )),
                        Vector.empty,
                        Some(Position(931, 45, 34))
                      ),
                      Vector.empty,
                      Some(Position(926, 45, 29))
                    )),
                  Vector.empty,
                  Vector.empty,
                  Vector.empty,
                  Vector.empty,
                  Some(Position(900, 45, 3))
                )),
              Vector.empty,
              Vector.empty,
              Some(Position(872, 44, 1))
            ),
            OperationDefinition(
              OperationType.Query,
              None,
              Vector.empty,
              Vector.empty,
              Vector(
                Field(
                  None,
                  "unnamed",
                  Vector(
                    Argument(
                      "truthy",
                      BooleanValue(true, Vector.empty, Some(Position(970, 49, 19))),
                      Vector.empty,
                      Some(Position(962, 49, 11))
                    ),
                    Argument(
                      "falsey",
                      BooleanValue(false, Vector.empty, Some(Position(984, 49, 33))),
                      Vector.empty,
                      Some(Position(976, 49, 25))
                    )),
                  Vector.empty,
                  Vector.empty,
                  Vector.empty,
                  Vector.empty,
                  Some(Position(954, 49, 3))
                ),
                Field(
                  None,
                  "query",
                  Vector.empty,
                  Vector.empty,
                  Vector.empty,
                  Vector.empty,
                  Vector.empty,
                  Some(Position(994, 50, 3))
                ),
                InlineFragment(
                  None,
                  Vector(
                    Directive(
                      "skip",
                      Vector(
                        Argument(
                          "unless",
                          VariableValue("foo", Vector.empty, Some(Position(1021, 52, 21))),
                          Vector.empty,
                          Some(Position(1013, 52, 13))
                        )),
                      Vector.empty,
                      Some(Position(1007, 52, 7))
                    )),
                  Vector(
                    Field(
                      None,
                      "id",
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Some(Position(1033, 53, 5))
                    )),
                  Vector.empty,
                  Vector.empty,
                  Some(Position(1003, 52, 3))
                ),
                InlineFragment(
                  None,
                  Vector.empty,
                  Vector(
                    Field(
                      None,
                      "id",
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Some(Position(1052, 56, 5))
                    )),
                  Vector.empty,
                  Vector.empty,
                  Some(Position(1042, 55, 3))
                )),
              Vector.empty,
              Vector.empty,
              Some(Position(950, 48, 1))
            )),
          Vector.empty,
          Some(Position(0, 1, 1)),
          None
        )

      QueryParser.parse(query) map (_.withoutSourceMapper) should be (Success(expectedAst))
    }

    "parse anonymous query" in {
      val query =
        """
          query {
            foo bar,
            baz
          }
        """

      val expectedAst =
        Document(Vector(
          OperationDefinition(
            OperationType.Query,
            None,
            Vector.empty,
            Vector.empty,
            Vector(
              Field(None, "foo", Vector.empty, Vector.empty, Vector.empty, Vector.empty, Vector.empty, Some(Position(31, 3, 13))),
              Field(None, "bar", Vector.empty, Vector.empty, Vector.empty, Vector.empty, Vector.empty, Some(Position(35, 3, 17))),
              Field(None, "baz", Vector.empty, Vector.empty, Vector.empty, Vector.empty, Vector.empty, Some(Position(52, 4, 13)))),
            Vector.empty,
            Vector.empty,
            Some(Position(11, 2, 11)))),
          Vector.empty,
          Some(Position(11, 2, 11)),
          None)

      QueryParser.parse(stripCarriageReturns(query)) map (_.copy(sourceMapper = None)) should be (Success(expectedAst))
    }

    "parse inline fragments without type condition" in {
      val query =
        """
          query {
            ... {
              foo bar
            }

            ... @include(if: true) {
              baz
            }
          }
        """

      val expectedAst =
        Document(Vector(
          OperationDefinition(OperationType.Query, None, Vector.empty, Vector.empty, Vector(
            InlineFragment(None, Vector.empty, Vector(
              Field(None, "foo", Vector.empty, Vector.empty, Vector.empty, Vector.empty, Vector.empty, Some(Position(51, 4, 15))),
              Field(None, "bar", Vector.empty, Vector.empty, Vector.empty, Vector.empty, Vector.empty, Some(Position(55, 4, 19)))),
              Vector.empty,
              Vector.empty,
              Some(Position(31, 3, 13))),
            InlineFragment(None,
              Vector(Directive("include", Vector(
                Argument("if", BooleanValue(true, Vector.empty, Some(Position(103, 7, 30))),
                  Vector.empty, Some(Position(99, 7, 26)))),
                Vector.empty,
                Some(Position(90, 7, 17)))),
              Vector(Field(None, "baz", Vector.empty, Vector.empty, Vector.empty, Vector.empty, Vector.empty, Some(Position(125, 8, 15)))),
              Vector.empty,
              Vector.empty,
              Some(Position(86, 7, 13)))),
            Vector.empty,
            Vector.empty,
            Some(Position(11, 2, 11)))),
          Vector.empty,
          Some(Position(11, 2, 11)), None)

      QueryParser.parse(stripCarriageReturns(query)) map (_.copy(sourceMapper = None)) should be (Success(expectedAst))
    }

    "parse anonymous mutation" in {
      val query =
        """
          mutation {
            foo bar,
            baz
          }
        """

      val expectedAst =
        Document(Vector(
          OperationDefinition(
            OperationType.Mutation,
            None,
            Vector.empty,
            Vector.empty,
            Vector(
              Field(None, "foo", Vector.empty, Vector.empty, Vector.empty, Vector.empty, Vector.empty, Some(Position(34, 3, 13))),
              Field(None, "bar", Vector.empty, Vector.empty, Vector.empty, Vector.empty, Vector.empty, Some(Position(38, 3, 17))),
              Field(None, "baz", Vector.empty, Vector.empty, Vector.empty, Vector.empty, Vector.empty, Some(Position(55, 4, 13)))),
            Vector.empty,
            Vector.empty,
            Some(Position(11, 2, 11)))),
          Vector.empty,
          Some(Position(11, 2, 11)),
          None)

      QueryParser.parse(stripCarriageReturns(query)) map (_.copy(sourceMapper = None)) should be (Success(expectedAst))
    }

    "provide useful error message (fragment `on`)" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        """
          { ...MissingOn }
          fragment MissingOn Type
        """)

      error.formattedError should equal (
        """Invalid input 'T', expected TypeCondition (line 3, column 30):
          |          fragment MissingOn Type
          |                             ^""".stripMargin) (after being strippedOfCarriageReturns)
    }

    "provide useful error message (braces)" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        "{ field: {} }")

      error.formattedError should equal (
        """Invalid input "{ field: {", expected OperationDefinition, FragmentDefinition or TypeSystemDefinition (line 1, column 1):
          |{ field: {} }
          |^""".stripMargin) (after being strippedOfCarriageReturns)
    }

    "provide useful error message (operation def)" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        "notanoperation Foo { field }")

      error.formattedError should equal (
        """Invalid input 'n', expected OperationDefinition, FragmentDefinition or TypeSystemDefinition (line 1, column 1):
          |notanoperation Foo { field }
          |^""".stripMargin) (after being strippedOfCarriageReturns)
    }

    "provide useful error message (ellipsis)" in {
      val Failure(error: SyntaxError) = QueryParser.parse("...")

      error.formattedError should equal (
        """Invalid input '.', expected OperationDefinition, FragmentDefinition or TypeSystemDefinition (line 1, column 1):
          |...
          |^""".stripMargin) (after being strippedOfCarriageReturns)
    }

    "parses constant default values" in {
      QueryParser.parse("{ field(complex: { a: { b: [ $var ] } }) }").isSuccess should be (true)
    }

    "parses variable inline values" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        "query Foo($x: Complex = { a: { b: [ $var ] } }) { field }")

      error.getMessage should equal (
        """Syntax error while parsing GraphQL query. Invalid input '$', expected StringValue, BooleanValue, ObjectValueConst, NullValue, ListValueConst, EnumValue or NumberValue (line 1, column 37):
          |query Foo($x: Complex = { a: { b: [ $var ] } }) { field }
          |                                    ^""".stripMargin) (after being strippedOfCarriageReturns)
    }

    "produce parse error for `1.`" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        "query Foo($x: Complex = 1.) { field }")

      error.formattedError should equal (
        """Invalid input "1.)", expected ValueConst or VariableDefinition (line 1, column 25):
          |query Foo($x: Complex = 1.) { field }
          |                        ^""".stripMargin) (after being strippedOfCarriageReturns)
    }

    "produce parse error for `.123`" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        "query Foo($x: Complex = .123) { field }")

      error.formattedError should equal (
        """Invalid input '.', expected StringValue, BooleanValue, ObjectValueConst, NullValue, ListValueConst, EnumValue or NumberValue (line 1, column 25):
          |query Foo($x: Complex = .123) { field }
          |                        ^""".stripMargin) (after being strippedOfCarriageReturns)
    }

    "produce parse error for `1.0e`" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        "query Foo($x: Complex = 1.0e) { field }")

      error.formattedError should equal (
        """Invalid input "1.0e)", expected ValueConst or VariableDefinition (line 1, column 25):
          |query Foo($x: Complex = 1.0e) { field }
          |                        ^""".stripMargin) (after being strippedOfCarriageReturns)
    }

    "produce parse error for `1.A`" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        "query Foo($x: Complex = 1.A) { field }")

      error.formattedError should equal (
        """Invalid input "1.A", expected ValueConst or VariableDefinition (line 1, column 25):
          |query Foo($x: Complex = 1.A) { field }
          |                        ^""".stripMargin) (after being strippedOfCarriageReturns)
    }

    "produce parse error for `+1`" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        "query Foo($x: Complex = +1) { field }")

      error.formattedError should equal (
        """Invalid input '+', expected StringValue, BooleanValue, ObjectValueConst, NullValue, ListValueConst, EnumValue or NumberValue (line 1, column 25):
          |query Foo($x: Complex = +1) { field }
          |                        ^""".stripMargin) (after being strippedOfCarriageReturns)
    }

    "produce parse error for `1.0eA`" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        "query Foo($x: Complex = 1.0eA) { field }")

      error.formattedError should equal (
        """Invalid input "1.0eA", expected ValueConst or VariableDefinition (line 1, column 25):
          |query Foo($x: Complex = 1.0eA) { field }
          |                        ^""".stripMargin) (after being strippedOfCarriageReturns)
    }

    "disallows uncommon control characters" in {
      QueryParser.parse("{ field\u0007 }").isSuccess should be (false)
      QueryParser.parse("{ field } \u0007").isSuccess should be (false)
    }

    "accepts BOM header" in {
      QueryParser.parse("\uFEFF{ field }").isSuccess should be (true)
    }

    "accepts new lines header" in {
      QueryParser.parse("{ field \n another }").isSuccess should be (true)
      QueryParser.parse("{ field \r\n another }").isSuccess should be (true)
    }

    "accepts escape sequences" in {
      QueryParser.parse("{ field(id: \"\\u000A\") }").isSuccess should be (true)
      QueryParser.parse("{ field(id: \"\\uXXXX\") }").isSuccess should be (false)
      QueryParser.parse("{ field(id: \"\\x\") }").isSuccess should be (false)
    }

    "allow `null` to be the prefix of an enum value" in {
      QueryParser.parse("query Foo($x: Complex = null111) { field }").isSuccess should be (true)
      QueryParser.parse("query Foo($x: Complex = null_foo) { field }").isSuccess should be (true)
      QueryParser.parse("query Foo($x: Complex = nullFoo) { field }").isSuccess should be (true)
    }

    "parse leading vertical bar in union types" in {
      val Success(ast) = QueryParser.parse("union Hello = | Wo | Rld")

      ast.withoutSourceMapper should be (
        Document(
          Vector(
            UnionTypeDefinition(
              "Hello",
              Vector(
                NamedType("Wo", Some(Position(16, 1, 17))),
                NamedType("Rld", Some(Position(21, 1, 22)))),
              Vector.empty,
              Vector.empty,
              Some(Position(0, 1, 1))
            )),
          Vector.empty,
          Some(Position(0, 1, 1)),
          None))
    }

    "not parse invalid usage of vertical bar on union types" in {
      QueryParser.parse("union Hello = |").isSuccess should be (false)
      QueryParser.parse("union Hello = Wo | Rld |").isSuccess should be (false)
      QueryParser.parse("union Hello = || Wo | Rld").isSuccess should be (false)
      QueryParser.parse("union Hello = Wo || Rld").isSuccess should be (false)
      QueryParser.parse("union Hello = | Wo | Rld ||").isSuccess should be (false)
    }

    "parse leading vertical bar in directive definitions" in {
      val Success(ast) = QueryParser.parse(
        """
        directive @include2(if: Boolean!) on
          | FIELD
          | FRAGMENT_SPREAD
          | INLINE_FRAGMENT
        """.stripCR)

      ast.withoutSourceMapper should be (
        Document(
          Vector(
            DirectiveDefinition(
              "include2",
              Vector(
                InputValueDefinition("if", NotNullType(NamedType("Boolean", Some(Position(33, 2, 33))), Some(Position(33, 2, 33))), None, Vector.empty, Vector.empty, Some(Position(29, 2, 29)))),
              Vector(
                DirectiveLocation("FIELD", Vector.empty, Some(Position(58, 3, 13))),
                DirectiveLocation("FRAGMENT_SPREAD", Vector.empty, Some(Position(76, 4, 13))),
                DirectiveLocation("INLINE_FRAGMENT", Vector.empty, Some(Position(104, 5, 13)))),
              Vector.empty,
              Some(Position(9, 2, 9))
            )),
          Vector.empty,
          Some(Position(9, 2, 9)),
          None))
    }

    def findAst[T <: AstNode : ClassTag](ast: AstNode): Option[T] =
      ast match {
        case node if implicitly[ClassTag[T]].runtimeClass.isAssignableFrom(node.getClass) ⇒ Some(node.asInstanceOf[T])
        case Document(defs, _, _, _) ⇒ defs map findAst[T] find (_.isDefined) flatten
        case OperationDefinition(_, _, vars, _, _, _, _, _) ⇒ vars map findAst[T] find (_.isDefined) flatten
        case VariableDefinition(_, _, default, _, _) ⇒ default flatMap findAst[T]
        case _ ⇒ None
      }

    "parse int values" in {
      val expectedTable = Vector(
        "4" → BigInt("4"),
        "-4" → BigInt("-4"),
        "9" → BigInt("9"),
        "0" → BigInt("0"),
        "784236564875237645762347623147574756321" → BigInt("784236564875237645762347623147574756321")
      )

      expectedTable foreach { expected ⇒
        findAst[BigIntValue](QueryParser.parse(s"query Foo($$x: Complex = ${expected._1}) { field }").get) should be (
          Some(BigIntValue(expected._2, Vector.empty, Some(Position(24, 1, 25)))))
      }
    }

    "parse float values" in {
      val expectedTable = Vector(
        "4.123" → BigDecimal("4.123"),
        "-4.123" → BigDecimal("-4.123"),
        "0.123" → BigDecimal("0.123"),
        "123E4" → BigDecimal("123E4"),
        "123e-4" → BigDecimal("123e-4"),
        "-1.123e4" → BigDecimal("-1.123e4"),
        "-1.123E4" → BigDecimal("-1.123E4"),
        "-1.123e+4" → BigDecimal("-1.123e+4"),
        "-1.123e4567" → BigDecimal("-1.123e4567")
      )

      expectedTable foreach { expected ⇒
        withClue(s"Parsing ${expected._1}.") {
          findAst[BigDecimalValue](QueryParser.parse(s"query Foo($$x: Complex = ${expected._1}) { field }").get) should be(
            Some(BigDecimalValue(expected._2, Vector.empty, Some(Position(24, 1, 25)))))
        }
      }
    }

    "parse input values independently" in {
      val expectedTable = Vector(
        "null" → NullValue(Vector.empty, Some(Position(0, 1, 1))),
        "1.234" → BigDecimalValue(BigDecimal("1.234"), Vector.empty, Some(Position(0, 1, 1))),
        "HELLO_WORLD" → EnumValue("HELLO_WORLD", Vector.empty, Some(Position(0, 1, 1))),
        "[1, 2 \"test\"]" → ListValue(
          Vector(
            BigIntValue(1, Vector.empty, Some(Position(1, 1, 2))),
            BigIntValue(2, Vector.empty, Some(Position(4, 1, 5))),
            StringValue("test", Vector.empty, Some(Position(6, 1, 7)))),
          Vector.empty,
          Some(Position(0, 1, 1))),
        "{a: 1, b: \"foo\" c: {nest: true, oops: null, e: FOO_BAR}}" →
          ObjectValue(
            Vector(
              ObjectField("a", BigIntValue(1, Vector.empty, Some(Position(4, 1, 5))), Vector.empty, Some(Position(1, 1, 2))),
              ObjectField("b", StringValue("foo", Vector.empty, Some(Position(10, 1, 11))), Vector.empty, Some(Position(7, 1, 8))),
              ObjectField("c",
                ObjectValue(
                  Vector(
                    ObjectField("nest", BooleanValue(true, Vector.empty, Some(Position(26, 1, 27))), Vector.empty, Some(Position(20, 1, 21))),
                    ObjectField("oops", NullValue(Vector.empty, Some(Position(38, 1, 39))), Vector.empty, Some(Position(32, 1, 33))),
                    ObjectField("e", EnumValue("FOO_BAR", Vector.empty, Some(Position(47, 1, 48))), Vector.empty, Some(Position(44, 1, 45)))),
                  Vector.empty,
                  Some(Position(19, 1, 20))),
                Vector.empty,
                Some(Position(16, 1, 17)))),
            Vector.empty,
            Some(Position(0, 1, 1))),
        """
         {
           a: 1

           # This is a test comment!
           b: "foo"
         }
        """ →
          ObjectValue(
            Vector(
              ObjectField("a", BigIntValue(1, Vector.empty, Some(Position(26, 3, 15))), Vector.empty, Some(Position(23, 3, 12))),
              ObjectField("b", StringValue("foo", Vector.empty, Some(Position(80, 6, 15))),
                Vector(Comment(" This is a test comment!", Some(Position(40, 5, 12)))),
                Some(Position(77, 6, 12)))),
              Vector.empty,
            Some(Position(10, 2, 10)))

      )

      expectedTable foreach { expected ⇒
        withClue(s"Parsing ${expected._1}.") {
          QueryParser.parseInput(stripCarriageReturns(expected._1)) should equal (Success(expected._2))
        }
      }
    }

    "parse and collect comments in AST nodes" in {
      val query = FileUtil loadQuery "too-many-comments.graphql"

      val expected =
        Document(
          Vector(
            OperationDefinition(
              OperationType.Query,
              Some("queryName"),
              Vector(
                VariableDefinition(
                  "foo",
                  NamedType("ComplexType", Some(Position(434, 23, 1))),
                  None,
                  Vector(
                    Comment(" comment 5", Some(Position(354, 15, 1))),
                    Comment(" comment 6", Some(Position(366, 16, 1)))),
                  Some(Position(378, 17, 1))
                ),
                VariableDefinition(
                  "site",
                  NamedType("Site", Some(Position(565, 36, 1))),
                  Some(EnumValue("MOBILE", Vector(Comment(" comment 16.5", Some(Position(602, 40, 1))), Comment(" comment 16.6", Some(Position(617, 41, 1)))), Some(Position(632, 42, 1)))),
                  Vector(
                    Comment(" comment 11", Some(Position(446, 24, 1))),
                    Comment(" comment 12", Some(Position(459, 25, 1))),
                    Comment(" comment 13", Some(Position(475, 28, 1))),
                    Comment(" comment 14", Some(Position(488, 29, 1)))),
                  Some(Position(501, 30, 1))
                ),
                VariableDefinition(
                  "foo",
                  NamedType("ComplexType", Some(Position(703, 48, 7))),
                  Some(ObjectValue(
                    Vector(
                      ObjectField(
                        "field1",
                        StringValue("val", Vector(Comment(" comment 18.11", Some(Position(849, 61, 1))), Comment(" comment 18.12", Some(Position(865, 62, 1)))), Some(Position(881, 63, 1))),
                        Vector(
                          Comment(" comment 18.7", Some(Position(779, 55, 1))),
                          Comment(" comment 18.8", Some(Position(794, 56, 1)))),
                        Some(Position(809, 57, 1))
                      ),
                      ObjectField(
                        "list",
                        ListValue(
                          Vector(
                            BigIntValue(1, Vector(Comment(" comment 18.21", Some(Position(1026, 76, 1))), Comment(" comment 18.22", Some(Position(1042, 77, 1)))), Some(Position(1058, 78, 1))),
                            BigIntValue(2, Vector(Comment(" comment 18.23", Some(Position(1061, 79, 1))), Comment(" comment 18.24", Some(Position(1077, 80, 1)))), Some(Position(1093, 81, 1))),
                            BigIntValue(3, Vector(Comment(" comment 18.25", Some(Position(1096, 82, 1))), Comment(" comment 18.26", Some(Position(1112, 83, 1)))), Some(Position(1128, 84, 1)))),
                          Vector(
                            Comment(" comment 18.19", Some(Position(992, 73, 1))),
                            Comment(" comment 18.20", Some(Position(1008, 74, 1)))),
                          Some(Position(1024, 75, 1))
                        ),
                        Vector(
                          Comment(" comment 18.13", Some(Position(887, 64, 1))),
                          Comment(" comment 18.14", Some(Position(903, 65, 1))),
                          Comment(" comment 18.15", Some(Position(921, 67, 1))),
                          Comment(" comment 18.16", Some(Position(937, 68, 1)))),
                        Some(Position(953, 69, 1))
                      ),
                      ObjectField(
                        "field2",
                        BooleanValue(true, Vector(Comment(" comment 18.35", Some(Position(1271, 97, 1))), Comment(" comment 18.36", Some(Position(1287, 98, 1)))), Some(Position(1303, 99, 1))),
                        Vector(
                          Comment(" comment 18.29", Some(Position(1164, 88, 1))),
                          Comment(" comment 18.30", Some(Position(1180, 89, 1))),
                          Comment(" comment 18.31", Some(Position(1198, 91, 1))),
                          Comment(" comment 18.32", Some(Position(1214, 92, 1)))),
                        Some(Position(1230, 93, 1))
                      )),
                    Vector(
                      Comment(" comment 18.5", Some(Position(747, 52, 1))),
                      Comment(" comment 18.6", Some(Position(762, 53, 1)))),
                    Some(Position(777, 54, 1))
                  )),
                  Vector(
                    Comment(" comment 17", Some(Position(639, 43, 1))),
                    Comment(" comment 18", Some(Position(652, 44, 1))),
                    Comment(" comment 18.1", Some(Position(667, 46, 1))),
                    Comment(" comment 18.2", Some(Position(682, 47, 1)))),
                  Some(Position(697, 48, 1))
                )),
              Vector.empty,
              Vector(
                Field(
                  Some("whoever123is"),
                  "node",
                  Vector(
                    Argument(
                      "id",
                      ListValue(
                        Vector(
                          BigIntValue(123, Vector(Comment(" comment 35", Some(Position(1660, 130, 3))), Comment(" comment 36", Some(Position(1675, 131, 3)))), Some(Position(1690, 132, 3))),
                          BigIntValue(456, Vector(Comment(" comment 37", Some(Position(1696, 133, 3))), Comment(" comment 38", Some(Position(1711, 134, 3)))), Some(Position(1726, 135, 3)))),
                        Vector(
                          Comment(" comment 33", Some(Position(1626, 127, 3))),
                          Comment(" comment 34", Some(Position(1641, 128, 3)))),
                        Some(Position(1656, 129, 3))
                      ),
                      Vector(
                        Comment(" comment 29", Some(Position(1557, 121, 3))),
                        Comment(" comment 30", Some(Position(1572, 122, 3)))),
                      Some(Position(1587, 123, 3))
                    )),
                  Vector.empty,
                  Vector(
                    Field(
                      None,
                      "id",
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Vector(
                        Comment(" comment 44", Some(Position(1837, 145, 4))),
                        Comment(" comment 45", Some(Position(1853, 146, 4)))),
                      Vector.empty,
                      Some(Position(1870, 147, 5))
                    ),
                    InlineFragment(
                      Some(NamedType("User", Some(Position(1996, 156, 5)))),
                      Vector(
                        Directive(
                          "defer",
                          Vector.empty,
                          Vector(
                            Comment(" comment 52", Some(Position(2005, 157, 5))),
                            Comment(" comment 53", Some(Position(2022, 158, 5)))),
                          Some(Position(2039, 159, 5))
                        )),
                      Vector(
                        Field(
                          None,
                          "field2",
                          Vector.empty,
                          Vector.empty,
                          Vector(
                            Field(
                              Some("alias"),
                              "field1",
                              Vector(
                                Argument(
                                  "first",
                                  BigIntValue(10, Vector(Comment(" comment 70", Some(Position(2474, 185, 9))), Comment(" comment 71", Some(Position(2495, 186, 9)))), Some(Position(2516, 187, 9))),
                                  Vector(
                                    Comment(" comment 66", Some(Position(2366, 179, 9))),
                                    Comment(" comment 67", Some(Position(2387, 180, 9)))),
                                  Some(Position(2408, 181, 9))
                                ),
                                Argument(
                                  "after",
                                  VariableValue("foo", Vector(Comment(" comment 76", Some(Position(2636, 194, 9))), Comment(" comment 77", Some(Position(2657, 195, 9)))), Some(Position(2678, 196, 9))),
                                  Vector(
                                    Comment(" comment 72", Some(Position(2528, 188, 9))),
                                    Comment(" comment 73", Some(Position(2549, 189, 9)))),
                                  Some(Position(2570, 190, 9))
                                )),
                              Vector(
                                Directive(
                                  "include",
                                  Vector(
                                    Argument(
                                      "if",
                                      VariableValue("foo", Vector(Comment(" comment 88", Some(Position(2961, 212, 10))), Comment(" comment 89", Some(Position(2983, 213, 10)))), Some(Position(3005, 214, 10))),
                                      Vector(
                                        Comment(" comment 84", Some(Position(2855, 206, 9))),
                                        Comment(" comment 85", Some(Position(2876, 207, 9)))),
                                      Some(Position(2897, 208, 9))
                                    )),
                                  Vector(
                                    Comment(" comment 80", Some(Position(2744, 200, 9))),
                                    Comment(" comment 81", Some(Position(2765, 201, 9)))),
                                  Some(Position(2786, 202, 9))
                                )),
                              Vector(
                                Field(
                                  None,
                                  "id",
                                  Vector.empty,
                                  Vector.empty,
                                  Vector.empty,
                                  Vector(
                                    Comment(" comment 94", Some(Position(3130, 221, 11))),
                                    Comment(" comment 95", Some(Position(3153, 222, 11)))),
                                  Vector.empty,
                                  Some(Position(3176, 223, 11))
                                ),
                                FragmentSpread("frag", Vector.empty, Vector(Comment(" comment 96", Some(Position(3190, 224, 11))), Comment(" comment 97", Some(Position(3213, 225, 11)))), Some(Position(3237, 227, 11)))),
                              Vector(
                                Comment(" comment 58", Some(Position(2151, 167, 7))),
                                Comment(" comment 59", Some(Position(2170, 168, 7)))),
                              Vector(
                                Comment(" comment 100", Some(Position(3312, 231, 11))),
                                Comment(" comment 101", Some(Position(3336, 232, 11)))),
                              Some(Position(2191, 169, 9))
                            )),
                          Vector.empty,
                          Vector(
                            Comment(" comment 102", Some(Position(3368, 234, 9))),
                            Comment(" comment 103", Some(Position(3390, 235, 9)))),
                          Some(Position(2092, 163, 7))
                        )),
                      Vector(
                        Comment(" comment 46", Some(Position(1879, 148, 5))),
                        Comment(" comment 47", Some(Position(1896, 149, 5)))),
                      Vector(
                        Comment(" comment 104", Some(Position(3418, 237, 7))),
                        Comment(" comment 105", Some(Position(3438, 238, 7)))),
                      Some(Position(1913, 150, 5))
                    )),
                  Vector(
                    Comment(" comment 21", Some(Position(1408, 109, 2))),
                    Comment(" comment 22", Some(Position(1422, 110, 2)))),
                  Vector(
                    Comment(" comment 106", Some(Position(3462, 240, 5))),
                    Comment(" comment 107", Some(Position(3480, 241, 5)))),
                  Some(Position(1437, 111, 3))
                )),
              Vector(
                Comment(" Copyright (c) 2015, Facebook, Inc.", Some(Position(0, 1, 1))),
                Comment(" All rights reserved.", Some(Position(37, 2, 1))),
                Comment("", Some(Position(60, 3, 1))),
                Comment(" This source code is licensed under the BSD-style license found in the", Some(Position(62, 4, 1))),
                Comment(" LICENSE file in the root directory of this source tree. An additional grant", Some(Position(134, 5, 1))),
                Comment(" of patent rights can be found in the PATENTS file in the same directory.", Some(Position(212, 6, 1)))),
              Vector(
                Comment(" comment 108", Some(Position(3500, 243, 3))),
                Comment(" comment 109", Some(Position(3516, 244, 3)))),
              Some(Position(288, 8, 1))
            ),
            OperationDefinition(
              OperationType.Mutation,
              Some("likeStory"),
              Vector.empty,
              Vector.empty,
              Vector(
                Field(
                  None,
                  "like",
                  Vector(
                    Argument(
                      "story",
                      BigIntValue(123, Vector(Comment(" comment 124", Some(Position(3793, 268, 3))), Comment(" comment 125", Some(Position(3809, 269, 3)))), Some(Position(3825, 270, 3))),
                      Vector(
                        Comment(" comment 120", Some(Position(3717, 262, 3))),
                        Comment(" comment 121", Some(Position(3733, 263, 3)))),
                      Some(Position(3749, 264, 3))
                    )),
                  Vector(
                    Directive(
                      "defer",
                      Vector.empty,
                      Vector(
                        Comment(" comment 128", Some(Position(3867, 274, 3))),
                        Comment(" comment 129", Some(Position(3883, 275, 3)))),
                      Some(Position(3899, 276, 3))
                    )),
                  Vector(
                    Field(
                      None,
                      "story",
                      Vector.empty,
                      Vector.empty,
                      Vector(
                        Field(
                          None,
                          "id",
                          Vector.empty,
                          Vector.empty,
                          Vector.empty,
                          Vector(
                            Comment(" comment 136", Some(Position(4030, 286, 5))),
                            Comment(" comment 137", Some(Position(4048, 287, 5))),
                            Comment(" comment 138", Some(Position(4067, 289, 5))),
                            Comment(" comment 139", Some(Position(4085, 290, 5)))),
                          Vector.empty,
                          Some(Position(4105, 291, 7))
                        )),
                      Vector(
                        Comment(" comment 132", Some(Position(3944, 280, 3))),
                        Comment(" comment 133", Some(Position(3960, 281, 3)))),
                      Vector(
                        Comment(" comment 140", Some(Position(4114, 292, 7))),
                        Comment(" comment 141", Some(Position(4134, 293, 7)))),
                      Some(Position(3978, 282, 5))
                    )),
                  Vector(
                    Comment(" comment 116", Some(Position(3644, 256, 1))),
                    Comment(" comment 117", Some(Position(3658, 257, 1)))),
                  Vector(
                    Comment(" comment 142", Some(Position(4158, 295, 5))),
                    Comment(" comment 143", Some(Position(4176, 296, 5)))),
                  Some(Position(3674, 258, 3))
                )),
              Vector(
                Comment(" comment 110", Some(Position(3536, 247, 4))),
                Comment(" comment 111", Some(Position(3553, 248, 4)))),
              Vector(
                Comment(" comment 144", Some(Position(4196, 298, 3))),
                Comment(" comment 145", Some(Position(4212, 299, 3)))),
              Some(Position(3567, 249, 1))
            ),
            FragmentDefinition(
              "frag",
              NamedType("Friend", Some(Position(4358, 312, 1))),
              Vector.empty,
              Vector(
                InlineFragment(
                  None,
                  Vector(
                    Directive(
                      "skip",
                      Vector(
                        Argument(
                          "unless",
                          VariableValue("foo", Vector(Comment(" comment 168", Some(Position(4613, 334, 3))), Comment(" comment 169", Some(Position(4629, 335, 3)))), Some(Position(4645, 336, 3))),
                          Vector(
                            Comment(" comment 164", Some(Position(4536, 328, 3))),
                            Comment(" comment 165", Some(Position(4552, 329, 3)))),
                          Some(Position(4568, 330, 3))
                        )),
                      Vector(
                        Comment(" comment 160", Some(Position(4460, 322, 3))),
                        Comment(" comment 161", Some(Position(4476, 323, 3)))),
                      Some(Position(4492, 324, 3))
                    )),
                  Vector(
                    Field(
                      None,
                      "id",
                      Vector.empty,
                      Vector.empty,
                      Vector.empty,
                      Vector(
                        Comment(" comment 174", Some(Position(4724, 343, 3))),
                        Comment(" comment 175", Some(Position(4740, 344, 3)))),
                      Vector.empty,
                      Some(Position(4758, 345, 5))
                    )),
                  Vector(
                    Comment(" comment 156", Some(Position(4395, 316, 1))),
                    Comment(" comment 157", Some(Position(4409, 317, 1))),
                    Comment(" comment 158", Some(Position(4424, 319, 1))),
                    Comment(" comment 159", Some(Position(4438, 320, 1)))),
                  Vector(
                    Comment(" comment 176", Some(Position(4765, 346, 5))),
                    Comment(" comment 177", Some(Position(4783, 347, 5)))),
                  Some(Position(4454, 321, 3))
                )),
              Vector(
                Comment(" comment 146", Some(Position(4228, 300, 3))),
                Comment(" comment 147", Some(Position(4242, 301, 1)))),
              Vector(
                Comment(" comment 178", Some(Position(4803, 349, 3))),
                Comment(" comment 179", Some(Position(4819, 350, 3)))),
              Some(Position(4257, 303, 1))
            )),
          Vector(
            Comment(" comment 180", Some(Position(4835, 352, 1))),
            Comment(" comment 181", Some(Position(4849, 353, 1)))),
          Some(Position(0, 1, 1)),
          None
        )

      QueryParser.parse(query) map (_.withoutSourceMapper) should be (Success(expected))
    }
  }

  "Ast" should {
    "be equal for the same queries" in {
      val query =
        """
          {
            id
            name
            friends {
              name
            }
          }
        """

      (QueryParser.parse(query) == QueryParser.parse(query)) should be (true)
    }

    "not be equal for the same queries with different AST node positions" in {
      val query1 =
        """
          {
            id
            name
            friends {
              name
            }
          }
        """

      val query2 =
        """
          {
            id
            name
            friends {name}
          }
        """

      (QueryParser.parse(query1) == QueryParser.parse(query2)) should be (false)
    }
  }
}