package es.eriktorr
package fleet.shared.time

import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, ZonedDateTime, ZoneId}

object ZonedDateTimeExtensions:
  extension (zonedDateTime: ZonedDateTime)
    def ageAt(timezone: String): String =
      import scala.language.unsafeNulls
      val now = LocalDateTime.now(ZoneId.of(timezone))
      val localDateTime = zonedDateTime.toInstant
        .atZone(ZoneId.of(timezone))
        .toLocalDateTime
      (
        (ChronoUnit.YEARS.between(localDateTime, now), "years"),
        (ChronoUnit.MONTHS.between(localDateTime, now), "months"),
        (ChronoUnit.DAYS.between(localDateTime, now), "days"),
        (ChronoUnit.HOURS.between(localDateTime, now), "hours"),
        (ChronoUnit.MINUTES.between(localDateTime, now), "minutes"),
        (ChronoUnit.SECONDS.between(localDateTime, now), "seconds"),
      ).toList
        .find { case (diff, _) => diff > 0L }
        .map { case (diff, units) => s"$diff $units" }
        .getOrElse("No age? maybe is a future date")

    def dateAt(timezone: String): String = TemporalFormatter.toString(zonedDateTime, timezone)
