package es.eriktorr
package fleet.adapter.persistence

import fleet.adapter.persistence.JdbcTripRepositorySuite.testCaseGen
import fleet.adapter.persistence.row.*
import fleet.domain.model.Trip
import fleet.domain.model.TripGenerators.*
import fleet.shared.adapter.persistence.MySqlTestTransactor
import fleet.shared.application.JdbcTestConfig
import fleet.shared.spec.CollectionGenerators.nDistinct
import fleet.shared.spec.MySqlSuite

import cats.implicits.{toFoldableOps, toTraverseOps}
import org.scalacheck.Gen
import org.scalacheck.cats.implicits.*
import org.scalacheck.effect.PropF.forAllF

final class JdbcTripRepositorySuite extends MySqlSuite:
  test("should list all trips"):
    forAllF(testCaseGen): testCase =>
      MySqlTestTransactor(JdbcTestConfig.FleetDatabase).testTransactorResource.use: transactor =>
        val companyTestRepository = JdbcCompanyTestRepository(transactor)
        val tripRepository = JdbcTripRepository(transactor)
        (for
          _ <- testCase.companyRows.traverse_(companyTestRepository.add)
          result <- tripRepository.listAll()
        yield result).assertEquals(testCase.expectedTrips)

object JdbcTripRepositorySuite:
  final private case class TestCase(
      branchRows: List[BranchRow],
      carRows: List[CarRow],
      companyRows: List[CompanyRow],
      customerRows: List[CustomerRow],
      driverRows: List[DriverRow],
      tripRows: List[TripRow],
      expectedTrips: List[Trip],
  )

  private val testCaseGen = for
    companyIds <- nDistinct(3, idGen)
    companies <- companyIds.traverse(id => companyGen(id))
    branchIds <- nDistinct(3, idGen)
    branches <- branchIds.traverse(id => branchGen(id, Gen.oneOf(companies)))
    carIds <- nDistinct(7, idGen)
    cars <- carIds.traverse(id => carGen(id, Gen.oneOf(branches)))
    customerIds <- nDistinct(7, idGen)
    customers <- customerIds.traverse(id => customerGen(id))
    driverIds <- nDistinct(7, idGen)
    drivers <- driverIds.traverse(id => driverGen(id))
    numTrips <- Gen.choose(3, 7)
    tripIds <- nDistinct(numTrips, idGen)
    trips <- tripIds.traverse(id =>
      tripGen(id, Gen.oneOf(cars), Gen.oneOf(drivers), Gen.oneOf(customers)),
    )
    branchRows = branches.map(branch => BranchRow(branch.id, branch.name, branch.timezone))
    carRows = cars.map(car =>
      CarRow(car.id, car.model, car.chassisNumber, car.color, car.registrationNumber),
    )
    companyRows = companies.map(company => CompanyRow(company.id, company.name, company.timezone))
    customerRows = customers.map(customer =>
      CustomerRow(customer.id, customer.name, customer.birthdate),
    )
    driverRows = drivers.map(driver => DriverRow(driver.id, driver.name, driver.licenseNumber))
    tripRows = trips.map(trip =>
      TripRow(trip.id, trip.timezone, trip.startOn, trip.endAt, trip.distance, trip.status),
    )
  yield TestCase(branchRows, carRows, companyRows, customerRows, driverRows, tripRows, trips)
