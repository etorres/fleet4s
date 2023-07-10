package es.eriktorr
package fleet.adapter.persistence

import fleet.adapter.persistence.JdbcTripTestRepository.statusPut
import fleet.domain.model.{Status, Trip}

import cats.effect.IO
import doobie.Put
import doobie.hikari.HikariTransactor
import doobie.implicits.javasql.*
import doobie.implicits.{toConnectionIOOps, toSqlInterpolator}

import java.sql.Timestamp
import java.time.ZonedDateTime

final class JdbcTripTestRepository(transactor: HikariTransactor[IO]):
  def add(trip: Trip): IO[Unit] =
    sql"""
         |INSERT INTO trip (
         |  id,
         |  timezone,
         |  start_on,
         |  end_at,
         |  distance,
         |  status,
         |  car_id,
         |  driver_id,
         |  customer_id
         |) VALUES (
         |  ${trip.id},
         |  ${trip.timezone},
         |  ${timestampFrom(trip.startOn)},
         |  ${timestampFrom(trip.endAt)},
         |  ${trip.distance},
         |  ${trip.status},
         |  ${trip.car.id},
         |  ${trip.driver.id},
         |  ${trip.customer.id}
         |)""".stripMargin.update.run
      .transact(transactor)
      .void

  private def timestampFrom(zonedDateTime: ZonedDateTime) =
    import scala.language.unsafeNulls
    Timestamp.valueOf(zonedDateTime.toLocalDateTime)

object JdbcTripTestRepository:
  given statusPut: Put[Status] = Put[String].tcontramap(_.toString)
