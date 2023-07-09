package es.eriktorr
package fleet.shared.time

import java.time.format.DateTimeFormatter
import java.time.{ZonedDateTime, ZoneId}

object TemporalFormatter:
  import scala.language.unsafeNulls

  private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

  def toString(zonedDateTime: ZonedDateTime, timezone: String): String = zonedDateTime.toInstant
    .atZone(ZoneId.of(timezone))
    .format(dateFormatter)
