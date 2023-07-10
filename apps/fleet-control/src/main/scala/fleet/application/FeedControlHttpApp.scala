package es.eriktorr
package fleet.application

import fleet.adapter.rest.TripRestController
import fleet.domain.service.TripService
import fleet.shared.adapter.rest.HealthService

import cats.effect.IO
import org.http4s.dsl.io.*
import org.http4s.server.Router
import org.http4s.server.middleware.Logger as Http4sLogger
import org.http4s.{HttpApp, HttpRoutes}
import org.typelevel.log4cats.Logger

final class FeedControlHttpApp(
    healthService: HealthService,
    tripService: TripService,
    enableLogger: Boolean = false,
)(using
    logger: Logger[IO],
):
  private val livenessCheckEndpoint: HttpRoutes[IO] = HttpRoutes.of[IO] { case GET -> Root =>
    Ok(s"${healthService.serviceName} is live")
  }

  private val readinessCheckEndpoint: HttpRoutes[IO] = HttpRoutes.of[IO] { case GET -> Root =>
    healthService.isReady.ifM(
      ifTrue = Ok(s"${healthService.serviceName} is ready"),
      ifFalse = ServiceUnavailable(s"${healthService.serviceName} is not ready"),
    )
  }

  private val serviceKleisli: HttpRoutes[IO] = TripRestController(tripService).routes

  val httpApp: HttpApp[IO] = decorate(
    Router(
      "/api/v1" -> serviceKleisli,
      healthService.livenessPath -> livenessCheckEndpoint,
      healthService.readinessPath -> readinessCheckEndpoint,
    ).orNotFound,
  )

  private def decorate(httpApp: HttpApp[IO]) =
    if enableLogger then
      Http4sLogger.httpApp(
        logHeaders = true,
        logBody = true,
        redactHeadersWhen = _ => false,
        logAction = Some((msg: String) => logger.info(msg)),
      )(httpApp)
    else httpApp
