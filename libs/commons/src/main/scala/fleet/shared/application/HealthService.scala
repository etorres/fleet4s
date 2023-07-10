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
      IO.blocking(
        HealthServer(
          serviceName,
          healthConfig.port.value,
          healthConfig.livenessPath,
          healthConfig.readinessPath,
        ),
      ),
    ) { healthServer =>
      IO.blocking(healthServer.markUnready())
        .timeout(5.seconds)
        .guarantee(IO.blocking(healthServer.close()))
    }

object HealthService:
  opaque type ServiceName <: String :| NonEmptyString = String :| NonEmptyString

  object ServiceName:
    def apply(x: String :| NonEmptyString): ServiceName = x
