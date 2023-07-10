package es.eriktorr
package fleet.domain.model

import fleet.shared.spec.StringGenerators.alphaNumericStringBetween
import fleet.shared.spec.TemporalGenerators.{before, localDateGen, withinInstantRange}

import cats.collections.Range
import org.scalacheck.Gen

import java.time.*
import java.time.temporal.ChronoUnit
import java.util.TimeZone

object TripGenerators:
  val idGen: Gen[Long] = Gen.choose(1L, 1_000_000L)

  val nameGen: Gen[String] = alphaNumericStringBetween(3, 12)

  val timezoneGen: Gen[String] =
    import scala.language.unsafeNulls
    Gen.oneOf(TimeZone.getAvailableIDs.toList)

  def branchGen(idGen: Gen[Long] = idGen, companyGen: Gen[Company]): Gen[Branch] = for
    id <- idGen
    name <- nameGen
    timezone <- timezoneGen
    company <- companyGen
  yield Branch(id, name, timezone, company)

  def carGen(idGen: Gen[Long] = idGen, branchGen: Gen[Branch]): Gen[Car] = for
    id <- idGen
    model <- alphaNumericStringBetween(3, 12)
    chassisNumber <- alphaNumericStringBetween(3, 12)
    color <- alphaNumericStringBetween(3, 12)
    registrationNumber <- alphaNumericStringBetween(3, 12)
    branch <- branchGen
  yield Car(id, model, chassisNumber, color, registrationNumber, branch)

  def companyGen(idGen: Gen[Long] = idGen): Gen[Company] = for
    id <- idGen
    name <- nameGen
    timezone <- timezoneGen
  yield Company(id, name, timezone)

  def customerGen(idGen: Gen[Long] = idGen): Gen[Customer] = for
    id <- idGen
    name <- nameGen
    birthdate <- localDateGen
  yield Customer(id, name, birthdate)

  def driverGen(idGen: Gen[Long] = idGen): Gen[Driver] = for
    id <- idGen
    name <- nameGen
    licenseNumber <- alphaNumericStringBetween(3, 12)
  yield Driver(id, name, licenseNumber)

  val statusGen: Gen[Status] = Gen.oneOf(Status.values.toList)

  private val maxMySqlTimestamp =
    import scala.language.unsafeNulls
    LocalDate.of(2038, Month.JANUARY, 19).atStartOfDay().toInstant(ZoneOffset.UTC)

  /** Generates safe MySQL timestamps.
    *
    * @see
    *   [[https://dev.mysql.com/doc/refman/8.0/en/datetime.html The DATE, DATETIME, and TIMESTAMP Types]]
    */
  private val sqlInstantGen: Gen[Instant] =
    import scala.language.unsafeNulls
    before(maxMySqlTimestamp.minus(400L, ChronoUnit.DAYS))

  private def afterSqlInstantGen(instant: Instant): Gen[Instant] =
    import scala.language.unsafeNulls
    withinInstantRange(
      Range(instant.plus(1L, ChronoUnit.DAYS), maxMySqlTimestamp),
    )

  private def toSqlTimestamp(instant: Instant) =
    import scala.language.unsafeNulls
    instant.atZone(ZoneId.of("UTC")).truncatedTo(ChronoUnit.SECONDS)

  def tripGen(
      idGen: Gen[Long] = idGen,
      carGen: Gen[Car],
      driverGen: Gen[Driver],
      customerGen: Gen[Customer],
  ): Gen[Trip] = for
    id <- idGen
    timezone <- timezoneGen
    instant <- sqlInstantGen
    startOn = toSqlTimestamp(instant)
    endAt <- afterSqlInstantGen(instant).map(toSqlTimestamp)
    distance <- Gen.choose(1d, 1_000d)
    status <- statusGen
    car <- carGen
    driver <- driverGen
    customer <- customerGen
  yield Trip(id, timezone, startOn, endAt, distance, status, car, driver, customer)
