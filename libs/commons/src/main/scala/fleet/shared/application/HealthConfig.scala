package es.eriktorr
package fleet.shared.application

import fleet.shared.application.HealthConfig.{LivenessPath, ReadinessPath}
import fleet.shared.refined.Types.UrlPathSegment

import _root_.cats.Show
import io.github.iltotore.iron.*

final case class HealthConfig(livenessPath: LivenessPath, readinessPath: ReadinessPath)

object HealthConfig:
  opaque type LivenessPath <: String :| UrlPathSegment = String :| UrlPathSegment
  object LivenessPath:
    def apply(x: String :| UrlPathSegment): LivenessPath = x

  opaque type ReadinessPath <: String :| UrlPathSegment = String :| UrlPathSegment
  object ReadinessPath:
    def apply(x: String :| UrlPathSegment): ReadinessPath = x

  val defaultLivenessPath: LivenessPath = LivenessPath.apply("/healthz".refine)
  val defaultReadinessPath: ReadinessPath = ReadinessPath.apply("/ready".refine)

  given Show[HealthConfig] =
    import scala.language.unsafeNulls
    Show.show(config => s"""{
                           |liveness_path: ${config.livenessPath},
                           |readiness_path: ${config.readinessPath}
                           |}""".stripMargin.replaceAll("\\R", ""))
