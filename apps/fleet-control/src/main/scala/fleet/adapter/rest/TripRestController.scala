package es.eriktorr
package fleet.adapter.rest

import fleet.adapter.rest.response.TripResponse
import fleet.domain.service.TripService

import cats.effect.IO
import io.circe.Encoder
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.io.*
import org.http4s.{HttpRoutes, QueryParamDecoder}

final class TripRestController(tripService: TripService):
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "trips" :? TimezoneQueryParamMatcher(timezone) =>
      val response = for
        trips <- tripService.findAll()
        response = trips.map(trip => TripResponse.from(trip, timezone.getOrElse("UTC")))
      yield response
      Ok(response)
  }

  private object TimezoneQueryParamMatcher extends OptionalQueryParamDecoderMatcher[String]("tz")
