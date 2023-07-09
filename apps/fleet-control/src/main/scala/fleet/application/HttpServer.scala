package es.eriktorr
package fleet.application

import fleet.shared.application.HttpServerConfig

import cats.effect.{IO, Resource}
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.GZip

object HttpServer:
  def impl(httpApp: HttpApp[IO], httpServerConfig: HttpServerConfig): Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost(httpServerConfig.host)
      .withPort(httpServerConfig.port)
      .withHttpApp(GZip(httpApp))
      .build
