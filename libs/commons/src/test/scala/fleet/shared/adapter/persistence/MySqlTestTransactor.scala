package es.eriktorr
package fleet.shared.adapter.persistence

import fleet.shared.adapter.persistence.MySqlTestTransactor.MysqlTable
import fleet.shared.application.JdbcTestConfig

import cats.effect.{IO, Resource}
import cats.implicits.toFoldableOps
import doobie.hikari.HikariTransactor
import doobie.implicits.{toConnectionIOOps, toSqlInterpolator}
import doobie.{Fragment, Put}

final class MySqlTestTransactor(jdbcTestConfig: JdbcTestConfig):
  val testTransactorResource: Resource[IO, HikariTransactor[IO]] = for
    transactor <- JdbcTransactor(jdbcTestConfig.config).transactorResource
    _ <- Resource.eval((for
      _ <- sql"SET FOREIGN_KEY_CHECKS = 0".update.run
      tables <- sql"""SELECT table_name
                      FROM information_schema.tables
                      WHERE table_schema = ${jdbcTestConfig.database}
                      AND table_type = 'BASE TABLE'"""
        .query[MysqlTable]
        .to[List]
      _ <- tables.traverse_(table =>
        Fragment.const(s"TRUNCATE TABLE ${table.table_name}").update.run,
      )
      _ <- sql"SET FOREIGN_KEY_CHECKS = 1".update.run
    yield ()).transact(transactor))
  yield transactor

object MySqlTestTransactor:
  final private case class MysqlTable(table_name: String)
