package es.eriktorr
package fleet.shared.spec

import org.scalacheck.Gen

object StringGenerators:
  def alphaNumericStringOfLength(length: Int): Gen[String] =
    stringOfLength(length, Gen.alphaNumChar)

  def alphaNumericStringBetween(minLength: Int, maxLength: Int): Gen[String] =
    stringBetween(minLength, maxLength, Gen.alphaNumChar)

  def nonEmptyAlphaNumericStringShorterThan(maxLength: Int): Gen[String] =
    nonEmptyStringShorterThan(maxLength, Gen.alphaNumChar)

  def stringBetween(minLength: Int, maxLength: Int, charGen: Gen[Char]): Gen[String] =
    for
      stringLength <- Gen.choose(minLength, maxLength)
      string <- stringOfLength(stringLength, charGen)
    yield string

  private def nonEmptyStringShorterThan(maxLength: Int, charGen: Gen[Char]): Gen[String] =
    stringBetween(1, maxLength, charGen)

  private def stringOfLength(length: Int, charGen: Gen[Char]): Gen[String] =
    Gen.listOfN(length, charGen).map(_.mkString)
