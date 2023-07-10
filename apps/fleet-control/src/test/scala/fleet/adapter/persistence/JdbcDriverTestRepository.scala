package es.eriktorr
package fleet.adapter.persistence

import fleet.domain.model.Driver

import cats.effect.IO
import doobie.hikari.HikariTransactor
import doobie.implicits.{toConnectionIOOps, toSqlInterpolator}

final class JdbcDriverTestRepository(transactor: HikariTransactor[IO]):
  def add(driver: Driver): IO[Unit] =
    sql"""
         |INSERT INTO driver (
         |  id,
         |  name,
         |  license_number
         |) VALUES (
         |  ${driver.id},
         |  ${driver.name},
         |  ${driver.licenseNumber}
         |)""".stripMargin.update.run
      .transact(transactor)
      .void
