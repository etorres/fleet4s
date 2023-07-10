package es.eriktorr
package fleet.adapter.persistence

import fleet.adapter.persistence.JdbcTripRepositorySuite.testCaseGen
import fleet.domain.model.*
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
        val branchTestRepository = JdbcBranchTestRepository(transactor)
        val carTestRepository = JdbcCarTestRepository(transactor)
        val driverTestRepository = JdbcDriverTestRepository(transactor)
        val customerTestRepository = JdbcCustomerTestRepository(transactor)
        val tripTestRepository = JdbcTripTestRepository(transactor)
        val tripRepository = JdbcTripRepository(transactor)
        (for
          _ <- testCase.companies.traverse_(companyTestRepository.add)
          _ <- testCase.branches.traverse_(branchTestRepository.add)
          _ <- testCase.cars.traverse_(carTestRepository.add)
          _ <- testCase.drivers.traverse_(driverTestRepository.add)
          _ <- testCase.customers.traverse_(customerTestRepository.add)
          _ <- testCase.trips.traverse_(tripTestRepository.add)
          result <- tripRepository.listAll()
          // TODO
          _ = result.sortBy(_.id).foreach(println)
          _ = testCase.expectedTrips.sortBy(_.id).foreach(println)
        // TODO
        yield result.sortBy(_.id)).assertEquals(testCase.expectedTrips.sortBy(_.id))

object JdbcTripRepositorySuite:
  final private case class TestCase(
      branches: List[Branch],
      cars: List[Car],
      companies: List[Company],
      drivers: List[Driver],
      customers: List[Customer],
      trips: List[Trip],
      expectedTrips: List[Trip],
  )

  private val testCaseGen = for
    companyIds <- nDistinct(3, idGen)
    companies <- companyIds.traverse(id => companyGen(id))
    branchIds <- nDistinct(3, idGen)
    branches <- branchIds.traverse(id => branchGen(id, Gen.oneOf(companies)))
    carIds <- nDistinct(7, idGen)
    cars <- carIds.traverse(id => carGen(id, Gen.oneOf(branches)))
    driverIds <- nDistinct(7, idGen)
    drivers <- driverIds.traverse(id => driverGen(id))
    customerIds <- nDistinct(7, idGen)
    customers <- customerIds.traverse(id => customerGen(id))
    numTrips <- Gen.choose(3, 7)
    tripIds <- nDistinct(numTrips, idGen)
    trips <- tripIds.traverse(id =>
      tripGen(id, Gen.oneOf(cars), Gen.oneOf(drivers), Gen.oneOf(customers)),
    )
  yield TestCase(branches, cars, companies, drivers, customers, trips, trips)
