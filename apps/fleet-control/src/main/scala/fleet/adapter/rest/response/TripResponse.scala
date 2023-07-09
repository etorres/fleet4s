package es.eriktorr
package fleet.adapter.rest.response

import fleet.domain.model.{Status, Trip}
import fleet.shared.time.ZonedDateTimeExtensions.{ageAt, dateAt}

import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import io.github.arainko.ducktape.*

final case class TripResponse(
    id: Long,
    timezone: String,
    startOn: String,
    endAt: String,
    recordAge: String,
    distance: Double,
    status: Status,
    carView: CarView,
    driverView: DriverView,
    customerView: CustomerView,
)

object TripResponse:
  def from(trip: Trip, timezone: String): TripResponse = trip
    .into[TripResponse]
    .transform(
      Field.computed(_.startOn, _.startOn.dateAt(timezone)),
      Field.computed(_.endAt, _.endAt.dateAt(timezone)),
      Field.computed(_.recordAge, _.startOn.ageAt(timezone)),
      Field.computed(_.carView, trip => CarView.from(trip.car)),
      Field.computed(_.driverView, trip => DriverView.from(trip.driver)),
      Field.computed(_.customerView, trip => CustomerView.from(trip.customer)),
    )

  given statusEncoder: Encoder[Status] = Encoder.encodeString.contramap(_.toString)

  given tripResponseEncoder: Encoder[TripResponse] = (response: TripResponse) =>
    Json.obj(
      ("id", response.id.asJson),
      ("record_timezone", response.timezone.asJson),
      ("start_on", response.startOn.asJson),
      ("end_at", response.startOn.asJson),
      ("record_age", response.recordAge.asJson),
      ("distance", response.distance.asJson),
      ("status", response.status.asJson),
      ("car", response.carView.asJson),
      ("driver", response.driverView.asJson),
      ("customer", response.customerView.asJson),
    )
