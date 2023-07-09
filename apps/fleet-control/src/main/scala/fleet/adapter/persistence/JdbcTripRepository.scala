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
  override def listAll(): IO[List[Trip]] =
    sql"""SELECT
         |  branch.id branchId,
         |  branch.name branchName,
         |  branch.timezone branchTimezone,
         |  car.id carId,
         |  car.model,
         |  car.chassis_number chassisNumber,
         |  car.color,
         |  car.registration_number registrationNumber,
         |  company.id companyId,
         |  company.name companyName,
         |  company.timezone companyTimezone,
         |  customer.id customerId,
         |  customer.name customerName,
         |  customer.birthdate,
         |  driver.id driverId,
         |  driver.name driverName,
         |  driver.license_number licenseNumber,
         |  trip.id tripId,
         |  trip.timezone tripTimezone,
         |  trip.start_on startOn,
         |  trip.end_at endAt,
         |  trip.distance,
         |  trip.status
         |FROM trip
         |INNER JOIN car ON trip.car_id = car.id
         |INNER JOIN branch ON car.branch_id = branch.id
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
