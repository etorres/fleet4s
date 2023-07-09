package es.eriktorr
package fleet.shared.time

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZonedDateTime, ZoneId}

object TemporalFormatter:
  import scala.language.unsafeNulls

  private val localDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
  private val localDateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def localDateFrom(rawDate: String): LocalDate = LocalDate.parse(rawDate, localDateFormatter)

  def toString(localDate: LocalDate): String = localDateFormatter.format(localDate)

  def toString(zonedDateTime: ZonedDateTime, timezone: String): String = zonedDateTime.toInstant
    .atZone(ZoneId.of(timezone))
    .format(localDateFormatter)

  def utcDateTimeFrom(rawDate: String): ZonedDateTime =
    ZonedDateTime.of(LocalDateTime.parse(rawDate, localDateTimeFormatter), ZoneId.of("UTC"))
