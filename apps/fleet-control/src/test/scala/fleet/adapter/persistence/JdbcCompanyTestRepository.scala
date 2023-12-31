package es.eriktorr
package fleet.adapter.persistence

import fleet.domain.model.Company

import cats.effect.IO
import doobie.hikari.HikariTransactor
import doobie.implicits.{toConnectionIOOps, toSqlInterpolator}

final class JdbcCompanyTestRepository(transactor: HikariTransactor[IO]):
  def add(company: Company): IO[Unit] =
    sql"""
         |INSERT INTO company (
         |  id,
         |  name,
         |  timezone
         |) VALUES (
         |  ${company.id},
         |  ${company.name},
         |  ${company.timezone}
         |)""".stripMargin.update.run
      .transact(transactor)
      .void
