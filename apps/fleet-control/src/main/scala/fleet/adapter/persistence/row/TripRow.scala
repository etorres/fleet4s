package es.eriktorr
package fleet.adapter.persistence.row

import fleet.domain.model.*

import doobie.Meta
import io.github.arainko.ducktape.*

import java.time.ZonedDateTime

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

  given tripRowMeta: Meta[TripRow] = ???
