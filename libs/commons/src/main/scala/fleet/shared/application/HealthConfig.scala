package es.eriktorr
package fleet.shared.application

import fleet.shared.application.HealthConfig.{LivenessPath, ReadinessPath}
import fleet.shared.refined.Types.NonEmptyString

import com.comcast.ip4s.{port, Port}
import io.github.iltotore.iron.*

final case class HealthConfig(
    livenessPath: LivenessPath,
    port: Port,
    readinessPath: ReadinessPath,
)

object HealthConfig:
  opaque type LivenessPath <: String :| NonEmptyString = String :| NonEmptyString
  object LivenessPath:
    def apply(x: String :| NonEmptyString): LivenessPath = x

  opaque type ReadinessPath <: String :| NonEmptyString = String :| NonEmptyString
  object ReadinessPath:
    def apply(x: String :| NonEmptyString): ReadinessPath = x

  val defaultLivenessPath: LivenessPath = LivenessPath.apply("/healthz")
  val defaultPort: Port = port"9990"
  val defaultReadinessPath: ReadinessPath = ReadinessPath.apply("/ready")