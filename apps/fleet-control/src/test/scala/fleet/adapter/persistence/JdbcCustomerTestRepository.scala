package es.eriktorr
package fleet.adapter.persistence

import fleet.domain.model.Customer
import fleet.shared.adapter.persistence.TemporalMapper.localDateMeta

import cats.effect.IO
import doobie.hikari.HikariTransactor
import doobie.implicits.{toConnectionIOOps, toSqlInterpolator}

final class JdbcCustomerTestRepository(transactor: HikariTransactor[IO]):
  def add(customer: Customer): IO[Unit] =
    sql"""
         |INSERT INTO customer (
         |  id,
         |  name,
         |  birthdate
         |) VALUES (
         |  ${customer.id},
         |  ${customer.name},
         |  ${customer.birthdate}
         |)""".stripMargin.update.run
      .transact(transactor)
      .void
