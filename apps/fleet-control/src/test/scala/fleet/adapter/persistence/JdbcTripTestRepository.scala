package es.eriktorr
package fleet.adapter.persistence

import fleet.adapter.persistence.JdbcTripTestRepository.statusPut
import fleet.domain.model.{Status, Trip}
import fleet.shared.adapter.persistence.TemporalMapper.utcDateTimeMeta

import cats.effect.IO
import doobie.Put
import doobie.hikari.HikariTransactor
import doobie.implicits.{toConnectionIOOps, toSqlInterpolator}

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
         |  ${trip.startOn},
         |  ${trip.endAt},
         |  ${trip.distance},
         |  ${trip.status},
         |  ${trip.car.id},
         |  ${trip.driver.id},
         |  ${trip.customer.id}
         |)""".stripMargin.update.run
      .transact(transactor)
      .void

object JdbcTripTestRepository:
  given statusPut: Put[Status] = Put[String].tcontramap(_.toString)
