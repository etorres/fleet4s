package es.eriktorr
package fleet.shared.adapter.rest

import fleet.shared.adapter.rest.HealthService.ServiceName
import fleet.shared.application.HealthConfig
import fleet.shared.application.HealthConfig.{LivenessPath, ReadinessPath}
import fleet.shared.refined.Types.NonEmptyString

import cats.effect.{IO, Ref, Resource}
import io.github.iltotore.iron.*
import org.typelevel.log4cats.Logger

final class HealthService(
    healthConfig: HealthConfig,
    readyRef: Ref[IO, Boolean],
    val serviceName: ServiceName,
)(using
    logger: Logger[IO],
):
  def isReady: IO[Boolean] = readyRef.get

  def livenessPath: LivenessPath = healthConfig.livenessPath

  def readinessPath: ReadinessPath = healthConfig.readinessPath

  def markReady: IO[Unit] = for
    _ <- logger.info("HealthService marked as ready")
    _ <- readyRef.set(true)
  yield ()

  private def markUnready: IO[Unit] = for
    _ <- logger.info("HealthService marked as unready")
    _ <- readyRef.set(false)
  yield ()

object HealthService:
  opaque type ServiceName <: String :| NonEmptyString = String :| NonEmptyString

  object ServiceName:
    def apply(x: String :| NonEmptyString): ServiceName = x

  def resourceWith(healthConfig: HealthConfig, serviceName: ServiceName, ready: Boolean = false)(
      using logger: Logger[IO],
  ): Resource[IO, HealthService] =
    Resource.make(for
      readyRef <- Ref.of[IO, Boolean](ready)
      healthService = HealthService(healthConfig, readyRef, serviceName)
    yield healthService)(_.markUnready)
