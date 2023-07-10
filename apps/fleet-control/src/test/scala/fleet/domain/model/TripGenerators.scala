package es.eriktorr
package fleet.domain.model

import fleet.shared.spec.StringGenerators.alphaNumericStringBetween
import fleet.shared.spec.TemporalGenerators.{after, instantGen, localDateGen}

import org.scalacheck.Gen

import java.time.temporal.ChronoUnit
import java.time.{Instant, ZonedDateTime, ZoneId}
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

  private def toSqlTimestamp(instant: Instant) =
    instant.atZone(ZoneId.of("UTC")).nn.truncatedTo(ChronoUnit.SECONDS).nn

  def tripGen(
      idGen: Gen[Long] = idGen,
      carGen: Gen[Car],
      driverGen: Gen[Driver],
      customerGen: Gen[Customer],
  ): Gen[Trip] = for
    id <- idGen
    timezone <- timezoneGen
    instant <- instantGen
    startOn = toSqlTimestamp(instant)
    endAt <- after(instant).map(toSqlTimestamp)
    distance <- Gen.choose(1d, 1_000d)
    status <- statusGen
    car <- carGen
    driver <- driverGen
    customer <- customerGen
  yield Trip(id, timezone, startOn, endAt, distance, status, car, driver, customer)
