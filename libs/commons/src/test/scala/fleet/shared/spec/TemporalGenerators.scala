package es.eriktorr
package fleet.shared.spec

import cats.collections.Range
import com.fortysevendeg.scalacheck.datetime.GenDateTime.genDateTimeWithinRange
import com.fortysevendeg.scalacheck.datetime.YearRange
import com.fortysevendeg.scalacheck.datetime.instances.jdk8.*
import com.fortysevendeg.scalacheck.datetime.jdk8.ArbitraryJdk8.*
import com.fortysevendeg.scalacheck.datetime.jdk8.granularity.seconds
import org.scalacheck.Gen

import java.time.*
import java.time.temporal.ChronoUnit
import scala.jdk.CollectionConverters.CollectionHasAsScala

object TemporalGenerators:
  import scala.language.unsafeNulls

  private given yearRange: YearRange = YearRange.between(1990, 2060)

  val instantGen: Gen[Instant] = arbInstantJdk8.arbitrary

  val localDateTimeGen: Gen[LocalDateTime] = arbLocalDateTimeJdk8.arbitrary

  val localDateGen: Gen[LocalDate] = localDateTimeGen.map(_.toLocalDate)

  val localDateTimeRangeGen: Gen[Range[LocalDateTime]] = for
    from <- localDateTimeGen
    to <- genDateTimeWithinRange(from.plusDays(1L), Duration.ofDays(365L))
  yield Range(from, to)

  val instantRangeGen: Gen[Range[Instant]] =
    localDateTimeRangeGen.map(_.map(_.toInstant(ZoneOffset.UTC)))

  val localDateRangeGen: Gen[Range[LocalDate]] = localDateTimeRangeGen.map(_.map(_.toLocalDate))

  def after(instant: Instant): Gen[Instant] = withinInstantRange(
    Range(instant.plus(1L, ChronoUnit.DAYS), instant.plus(365L, ChronoUnit.DAYS)),
  )

  def after(localDateTime: LocalDateTime): Gen[LocalDateTime] =
    withinLocalDateTimeRange(Range(localDateTime.plusDays(1L), localDateTime.plusYears(1L)))

  def after(localDate: LocalDate): Gen[LocalDate] =
    withinLocalDateRange(Range(localDate.plusDays(1L), localDate.plusYears(1L)))

  def before(instant: Instant): Gen[Instant] = withinInstantRange(
    Range(instant.minus(365L, ChronoUnit.DAYS), instant.minus(1L, ChronoUnit.DAYS)),
  )

  def outOfLocalDateTimeRange(
      localDateTimeRange: Range[LocalDateTime],
      max: Option[LocalDateTime] = None,
      min: Option[LocalDateTime] = None,
  ): Gen[LocalDateTime] =
    Gen.frequency(
      1 -> withinLocalDateTimeRange(
        Range(
          min.getOrElse(localDateTimeRange.start.minusYears(1L)),
          localDateTimeRange.start.minusDays(1L),
        ),
      ),
      1 -> withinLocalDateTimeRange(
        Range(
          localDateTimeRange.end.plusDays(1L),
          max.getOrElse(localDateTimeRange.end.plusYears(1L)),
        ),
      ),
    )

  def outOfLocalDateRange(
      dateRange: Range[LocalDate],
      max: Option[LocalDate] = None,
      min: Option[LocalDate] = None,
  ): Gen[LocalDate] =
    Gen.frequency(
      1 -> withinLocalDateRange(
        Range(min.getOrElse(dateRange.start.minusYears(1L)), dateRange.start.minusDays(1L)),
      ),
      1 -> withinLocalDateRange(
        Range(dateRange.end.plusDays(1L), max.getOrElse(dateRange.end.plusYears(1L))),
      ),
    )

  def outOfInstantRange(
      instantRange: Range[Instant],
      max: Option[Instant] = None,
      min: Option[Instant] = None,
  ): Gen[Instant] =
    outOfLocalDateTimeRange(
      instantRange.map(x => LocalDateTime.ofInstant(x, ZoneOffset.UTC)),
      max.map(x => LocalDateTime.ofInstant(x, ZoneOffset.UTC)),
      min.map(x => LocalDateTime.ofInstant(x, ZoneOffset.UTC)),
    ).map(_.toInstant(ZoneOffset.UTC))

  def withinInstantRange(instantRange: Range[Instant]): Gen[Instant] =
    genDateTimeWithinRange(
      LocalDateTime.ofInstant(instantRange.start, ZoneOffset.UTC),
      Duration.ofDays(ChronoUnit.DAYS.between(instantRange.start, instantRange.end)),
    ).map(_.toInstant(ZoneOffset.UTC))

  def withinLocalDateTimeRange(localDateTimeRange: Range[LocalDateTime]): Gen[LocalDateTime] =
    genDateTimeWithinRange(
      localDateTimeRange.start,
      Duration.ofDays(ChronoUnit.DAYS.between(localDateTimeRange.start, localDateTimeRange.end)),
    )

  def withinLocalDateRange(dateRange: Range[LocalDate]): Gen[LocalDate] =
    genDateTimeWithinRange(
      dateRange.start.atStartOfDay(),
      Duration.ofDays(ChronoUnit.DAYS.between(dateRange.start, dateRange.end)),
    ).map(_.toLocalDate)

  val zoneIdGen: Gen[String] = Gen.oneOf(ZoneId.getAvailableZoneIds.asScala)
