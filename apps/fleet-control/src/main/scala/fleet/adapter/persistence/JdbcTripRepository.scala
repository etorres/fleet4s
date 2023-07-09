package es.eriktorr
package fleet.adapter.persistence

import fleet.adapter.persistence.row.*
import fleet.domain.model.Trip
import fleet.domain.service.TripRepository

import cats.effect.IO
import doobie.hikari.HikariTransactor
import doobie.implicits.{toConnectionIOOps, toSqlInterpolator}

import java.time.ZonedDateTime

final class JdbcTripRepository(transactor: HikariTransactor[IO]) extends TripRepository:
  override def findAll(): IO[List[Trip]] =
    sql"""SELECT
         |  branch.id AS branchId,
         |  branch.name AS branchName,
         |  branch.timezone AS branchTimezone,
         |  car.id AS carId,
         |  car.model,
         |  car.chassis_number AS chassisNumber,
         |  car.color,
         |  car.registration_number AS registrationNumber,
         |  company.id AS companyId,
         |  company.name AS companyName,
         |  company.timezone AS companyTimezone,
         |  customer.id AS customerId,
         |  customer.name AS customerName,
         |  customer.birthdate
         |  driver.id AS driverId,
         |  driver.name AS driverName,
         |  driver.license_number AS licenseNumber,
         |  trip.id AS tripId,
         |  trip.timezone AS tripTimezone,
         |  trip.start_on AS startOn,
         |  trip.end_at AS endAt,
         |  trip.distance,
         |  trip.status
         |FROM trip
         |INNER JOIN cars ON trip.car_id = cars.id
         |INNER JOIN branch ON cars.branch_id = branch.id
         |INNER JOIN company ON branch.company_id = company.id
         |INNER JOIN driver ON trip.driver_id = driver.id
         |INNER JOIN customer ON trip.customer_id = customer.id""".stripMargin
      .query[(BranchRow, CarRow, CompanyRow, CustomerRow, DriverRow, TripRow)]
      .map { case (branchRow, carRow, companyRow, customerRow, driverRow, tripRow) =>
        val company = companyRow.toCompany
        val branch = branchRow.toBranch(company)
        val car = carRow.toCar(branch)
        val driver = driverRow.toDriver
        val customer = customerRow.toCustomer
        tripRow.toTrip(car, driver, customer)
      }
      .to[List]
      .transact(transactor)

  override def findAllByStartOnIsBetween(from: ZonedDateTime, to: ZonedDateTime): IO[List[Trip]] =
    IO.pure(List.empty)
