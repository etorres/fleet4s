package es.eriktorr
package fleet.adapter.persistence.row

import fleet.domain.model.*
import fleet.shared.adapter.persistence.TemporalMapper.utcDateTimeMeta

import cats.implicits.catsSyntaxEither
import doobie.{Get, Meta, Read}
import io.github.arainko.ducktape.*

import java.time.ZonedDateTime
import scala.util.Try

final case class TripRow(
    tripId: Long,
    tripTimezone: String,
    startOn: ZonedDateTime,
    endAt: ZonedDateTime,
    distance: Double,
    status: Status,
)

object TripRow:
  extension (tripRow: TripRow)
    def toTrip(car: Car, driver: Driver, customer: Customer): Trip =
      tripRow
        .into[Trip]
        .transform(
          Field.renamed(_.id, _.tripId),
          Field.renamed(_.timezone, _.tripTimezone),
          Field.const(_.car, car),
          Field.const(_.driver, driver),
          Field.const(_.customer, customer),
        )

  given statusRead: Get[Status] =
    Get[String].temap(x => Try(Status.valueOf(x)).toEither.leftMap(_.getMessage.nn))

  given tripRowRead: Read[TripRow] =
    Read[(Long, String, ZonedDateTime, ZonedDateTime, Double, Status)].map {
      case (id, timezone, startOn, endAt, distance, status) =>
        TripRow(id, timezone, startOn, endAt, distance, status)
    }
