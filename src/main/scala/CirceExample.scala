package example.circe

import cats.syntax.either._
import io.circe._
import io.circe.generic.JsonCodec
import io.circe.generic.auto._
import io.circe.generic.extras._
import io.circe.generic.semiauto._
import io.circe.parser._
import io.circe.syntax._

sealed trait Foo
case class Bar(xs: List[String]) extends Foo
case class Qux(i: Int, d: Option[Double]) extends Foo
case class Foobar(bar: Bar, qux: Qux, values: List[Int])

object CirceExample extends App {

  // config: io.circe.generic.extras.Configuration
  //implicit val config: Configuration = Configuration.default.withSnakeCaseKeys
  implicit val config: Configuration = Configuration.default.withDefaults

  @ConfiguredJsonCodec case class Name(
    @JsonKey("firstName") first: String,
    @JsonKey("lastName") last: String
  )

  @ConfiguredJsonCodec case class Address(
    @JsonKey("street") street: String,
    @JsonKey("building") building: String,
    @JsonKey("zipCode") zip: String
  )

  @ConfiguredJsonCodec case class Pub(
    @JsonKey("title") title: String,
    @JsonKey("year") year: Int,
    @JsonKey("author") author: String
  )

  @ConfiguredJsonCodec case class Alice(
    @JsonKey("age") i: Int,
    @JsonKey("name") n: Name,
    @JsonKey("address") addr: Address,
    @JsonKey("publication") pub: Pub,
    @JsonKey("tags") tags: List[String]
  )
  val x = Alice(
    21,
    Name("Alice", "Green"),
    Address("Some Road", "Some Building", "123456"),
    Pub("Programming Scala", 2015, "D. W."),
    List("fancy tag")
  );
  println(s"x.asJson.noSpaces = ${x.asJson.noSpaces}")

  // Parsing Json
  val rawJson: String = """
    {
      "foo": "bar",
      "baz": 123,
      "list of stuff": [ 4, 5, 6 ]
    }
    """

  // `parse(rawJson)` returns `Either[io.circe.ParsingFailure,io.circe.Json]`
  parse(rawJson) match {
    case Left(e) ⇒ println(e)
    case Right(v) ⇒ println(v.noSpaces)
  }
  val parseResult = parse(rawJson).right.getOrElse(Json.Null)

  parse("invalidJsonString") match {
    case Left(e) ⇒ println(e)
    case Right(v) ⇒ println(v.noSpaces)
  }

  // Traversing and modifying JSON
  val json: String = """
  {
    "id": "c730433b-082c-4984-9d66-855c243266f0",
    "name": "Foo",
    "counts": [1, 2, 3],
    "values": {
      "bar": true,
      "baz": 100.001,
      "qux": ["a", "b", "c"]
    }
  }
  """
  val doc: Json = parse(json).getOrElse(Json.Null)
  // In order to traverse the document we need to create an HCursor with the focus at the
  // document's root
  val cursor: HCursor = doc.hcursor
  // baz: io.circe.Decoder.Result[Double] = Right(100.001)
  val baz: Decoder.Result[Double] =
    cursor.downField("values").downField("baz").as[Double]
  println(s"baz=${baz}")

  // You can also use `get[A](key)` as shorthand for `downField(key).as[A]`
  // baz2: io.circe.Decoder.Result[Double] = Right(100.001)
  val baz2: Decoder.Result[Double] =
    cursor.downField("values").get[Double]("baz")
  println(s"baz2=${baz2.getOrElse(-1)}")

  val secondQux: List[String] = cursor
    .downField("values")
    .downField("qux")
    .as[Array[String]]
    .right
    .map(_.toList) // `.right` creates `EitherProjection` (see, `Programming Scala`, pp 232)
    .getOrElse(List())
  println(s"secondQux=${secondQux}")

  // secondQux: io.circe.Decoder.Result[String] = Right(b)
  val thirdQux: Decoder.Result[Array[String]] = cursor
    .downField("values")
    .downField("qux")
    .as[Array[String]] // Right([Ljava.lang.String;@539f4941)])
  println(s"thirdQux=${thirdQux}")

  // Transforming data
  // We can also use a cursor to modify JSON.
  val reversedNameCursor: ACursor = cursor.downField("name").withFocus(_.mapString(_.reverse))
  // We can then return to the root of the document and return its value with top
  val reversedName: Option[Json] = reversedNameCursor.top
  println(s"reversedName=${reversedName match {
    case Some(x) ⇒ x.noSpaces
    case None ⇒ None
  }}")

  // Encoding and decoding
  val intsJson: io.circe.Json = List(1, 2, 3).asJson
  println(s"intsJson=${intsJson}")
  val intsRight: Decoder.Result[List[Int]] = intsJson.as[List[Int]]
  println(s"intsRight=${intsRight}")

  val anotherInts: Either[io.circe.Error, List[Int]] = decode[List[Int]]("[1, 2, 3]")
  anotherInts match {
    case Left(e) ⇒ println(e)
    case Right(x) ⇒ println(s"anotherInts=$x")
  }

  // Semi-automatic derivation
  case class MyFoo(a: Int, b: String, c: Boolean)
  implicit val fooDecoder: Decoder[MyFoo] = deriveDecoder
  implicit val fooEncoder: Encoder[MyFoo] = deriveEncoder
  println(s"MyFoo=${MyFoo(1, "a", true).asJson.noSpaces}")

  // You will need the Macro Paradise plugin to use annotation macros like @JsonCodec
  @JsonCodec case class MyBar(i: Int, s: String)
  println(s"MyBar=${MyBar(13, "Qux").asJson}")

  // get values of keys with hyphen
  val jsonObject: Json = parse("""
    {
      "order": {
        "customer": {
          "name": "Custy McCustomer",
          "contact-Details": {
            "address": "1 Fake Street, London, England",
            "phone": "0123-456-789"
          }
        },
        "items": [{
          "id": 123,
          "description": "banana",
          "quantity": 1
        }, {
          "id": 456,
          "description": "apple",
          "quantity": 2
        }],
        "total": 123.45
      }
    }
    """).getOrElse(Json.Null)

  import io.circe.optics.JsonPath._
  val _phoneNum = root.order.customer.`contact-Details`.phone.string
  val phoneNum: Option[String] = _phoneNum.getOption(jsonObject)
  println(s"phoneNum = ${phoneNum}")
}
