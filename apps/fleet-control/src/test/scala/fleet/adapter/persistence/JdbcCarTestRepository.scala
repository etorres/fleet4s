package es.eriktorr
package fleet.adapter.persistence

import fleet.domain.model.Car

import cats.effect.IO
import doobie.hikari.HikariTransactor
import doobie.implicits.{toConnectionIOOps, toSqlInterpolator}

final class JdbcCarTestRepository(transactor: HikariTransactor[IO]):
  def add(car: Car): IO[Unit] =
    sql"""
         |INSERT INTO car (
         |  id,
         |  model,
         |  chassis_number,
         |  color,
         |  registration_number,
         |  branch_id
         |) VALUES (
         |  ${car.id},
         |  ${car.model},
         |  ${car.chassisNumber},
         |  ${car.color},
         |  ${car.registrationNumber},
         |  ${car.branch.id}
         |)""".stripMargin.update.run
      .transact(transactor)
      .void
