package es.eriktorr
package fleet.shared.application

import fleet.shared.application.HealthService.ServiceName
import fleet.shared.refined.Types.NonEmptyString

import ca.dvgi.healthful.HealthServer
import cats.effect.{IO, Resource}
import io.github.iltotore.iron.*

import scala.concurrent.duration.DurationInt

final class HealthService(healthConfig: HealthConfig, serviceName: ServiceName):
  val healthResource: Resource[IO, HealthServer] =
    Resource.make(
      for
        healthServer <- IO.blocking(
          HealthServer(
            serviceName,
            healthConfig.port.value,
            healthConfig.livenessPath,
            healthConfig.readinessPath,
          ),
        )
        _ <- IO.pure(healthServer.markReady())
      yield healthServer,
    ) { healthServer =>
      (for
        _ <- IO.blocking(healthServer.markUnready())
        _ <- IO.blocking(healthServer.close())
      yield ()).timeout(5.seconds)
    }

object HealthService:
  opaque type ServiceName <: String :| NonEmptyString = String :| NonEmptyString

  object ServiceName:
    def apply(x: String :| NonEmptyString): ServiceName = x
