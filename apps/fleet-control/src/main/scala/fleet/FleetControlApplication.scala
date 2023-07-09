package es.eriktorr
package fleet

import fleet.adapter.persistence.JdbcTripRepository
import fleet.application.{FeedControlHttpApp, FleetControlConfig, FleetControlParams, HttpServer}
import fleet.domain.service.TripService
import fleet.shared.adapter.persistence.JdbcTransactor

import cats.effect.{ExitCode, IO}
import cats.implicits.{catsSyntaxTuple2Semigroupal, showInterpolator}
import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object FleetControlApplication
    extends CommandIOApp(name = "fleet-control", header = "Fleet Control System REST API"):
  override def main: Opts[IO[ExitCode]] = (FleetControlConfig.opts, FleetControlParams.opts).mapN {
    case (config, params) =>
      program(config, params)
  }

  private def program(config: FleetControlConfig, params: FleetControlParams) = for
    logger <- Slf4jLogger.create[IO]
    _ <- logger.info(show"Starting HTTP server with configuration: $config")
    _ <- (for
      transactor <- JdbcTransactor(config.jdbcConfig).transactorResource
      tripRepository = JdbcTripRepository(transactor)
      tripService = TripService(tripRepository)
      given Logger[IO] = logger
      _ <- HttpServer.impl(
        FeedControlHttpApp(tripService, params.verbose).httpApp,
        config.httpServerConfig,
      )
    yield ()).use(_ => IO.never)
  yield ExitCode.Success