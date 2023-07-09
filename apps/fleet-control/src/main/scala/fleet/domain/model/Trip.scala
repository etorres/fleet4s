package es.eriktorr
package fleet.domain.model

import java.time.ZonedDateTime

final case class Trip(
    id: Long,
    timezone: String,
    startOn: ZonedDateTime,
    endAt: ZonedDateTime,
    distance: Double,
    status: Status,
    car: Car,
    driver: Driver,
    customer: Customer,
)
