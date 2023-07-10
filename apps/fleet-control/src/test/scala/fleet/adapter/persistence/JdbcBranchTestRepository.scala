package es.eriktorr
package fleet.adapter.persistence

import fleet.domain.model.Branch

import cats.effect.IO
import doobie.hikari.HikariTransactor
import doobie.implicits.{toConnectionIOOps, toSqlInterpolator}

final class JdbcBranchTestRepository(transactor: HikariTransactor[IO]):
  def add(branch: Branch): IO[Unit] =
    sql"""
         |INSERT INTO branch (
         |  id,
         |  name,
         |  timezone,
         |  company_id
         |) VALUES (
         |  ${branch.id},
         |  ${branch.name},
         |  ${branch.timezone},
         |  ${branch.company.id}
         |)""".stripMargin.update.run
      .transact(transactor)
      .void
