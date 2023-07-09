package es.eriktorr
package fleet.adapter.persistence

import fleet.adapter.persistence.row.CompanyRow

import cats.effect.IO
import doobie.hikari.HikariTransactor
import doobie.implicits.{toConnectionIOOps, toSqlInterpolator}

final class JdbcCompanyTestRepository(transactor: HikariTransactor[IO]):
  def add(row: CompanyRow): IO[Unit] =
    sql"""
         |INSERT INTO company (
         |  id,
         |  name,
         |  timezone
         |) VALUES (
         |  ${row.companyId},
         |  ${row.companyName},
         |  ${row.companyTimezone}
         |)""".stripMargin.update.run
      .transact(transactor)
      .void
