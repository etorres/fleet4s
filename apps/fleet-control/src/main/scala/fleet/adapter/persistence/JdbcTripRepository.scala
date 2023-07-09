package es.eriktorr
package fleet.adapter.persistence

import fleet.domain.model.Trip
import fleet.domain.service.TripRepository

import cats.effect.IO
import doobie.hikari.HikariTransactor
//import doobie.implicits.{toConnectionIOOps, toSqlInterpolator}

import java.time.ZonedDateTime

final class JdbcTripRepository(transactor: HikariTransactor[IO]) extends TripRepository:
  override def findAll(): IO[List[Trip]] =
    assert(transactor == transactor) // TODO
    IO.pure(List.empty)
//    sql"""SELECT * FROM table""".query[Trip].to[List].transact(transactor)

  override def findAllByStartOnIsBetween(from: ZonedDateTime, to: ZonedDateTime): IO[List[Trip]] =
    IO.pure(List.empty)
