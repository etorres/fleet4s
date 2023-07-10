package es.eriktorr
package fleet.shared.application.argument

import fleet.shared.application.HealthConfig.{LivenessPath, ReadinessPath}
import fleet.shared.refined.Types.UrlPathSegment

import cats.data.ValidatedNel
import com.monovore.decline.Argument
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.*

trait HealthConfigArgument:
  given livenessPathArgument: Argument[LivenessPath] = new Argument[LivenessPath]:
    override def read(string: String): ValidatedNel[String, LivenessPath] =
      string.refineValidatedNel[UrlPathSegment].map(LivenessPath.apply)

    override def defaultMetavar: String = "path"

  given readinessPathArgument: Argument[ReadinessPath] = new Argument[ReadinessPath]:
    override def read(string: String): ValidatedNel[String, ReadinessPath] =
      string.refineValidatedNel[UrlPathSegment].map(ReadinessPath.apply)

    override def defaultMetavar: String = "path"

object HealthConfigArgument extends HealthConfigArgument
