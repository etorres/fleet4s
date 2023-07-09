package es.eriktorr
package fleet.shared.adapter.persistence

import fleet.shared.time.TemporalExtensions.LocalDateExtensions.asString
import fleet.shared.time.TemporalExtensions.ZonedDateTimeExtensions.dateAtUtc
import fleet.shared.time.TemporalFormatter

import doobie.Meta

import java.time.{LocalDate, ZonedDateTime}

trait TemporalMapper:
  import scala.language.unsafeNulls

  given localDateMeta: Meta[LocalDate] =
    Meta.StringMeta.timap(TemporalFormatter.localDateFrom)(_.asString)

  given utcDateTimeMeta: Meta[ZonedDateTime] =
    Meta.StringMeta.timap(TemporalFormatter.utcDateTimeFrom)(_.dateAtUtc)

object TemporalMapper extends TemporalMapper
