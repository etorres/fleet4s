package es.eriktorr
package fleet.application

import fleet.adapter.rest.TripRestController
import fleet.domain.service.TripService

import cats.effect.IO
import org.http4s.dsl.io.*
import org.http4s.server.Router
import org.http4s.server.middleware.Logger as Http4sLogger
import org.http4s.{HttpApp, HttpRoutes}
import org.typelevel.log4cats.Logger

final class FeedControlHttpApp(tripService: TripService, enableLogger: Boolean = false)(using
    logger: Logger[IO],
):
  private val healthCheckEndpoint = HttpRoutes.of[IO] { case GET -> Root => Ok("Running") }

  private val serviceKleisli: HttpRoutes[IO] = TripRestController(tripService).routes

  val httpApp: HttpApp[IO] = decorate(
    Router("/api/v1" -> serviceKleisli, "/health" -> healthCheckEndpoint).orNotFound,
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
