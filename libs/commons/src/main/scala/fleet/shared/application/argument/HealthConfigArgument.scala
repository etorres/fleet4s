package es.eriktorr
package fleet.shared.application.argument

import fleet.shared.application.HealthConfig.{LivenessPath, ReadinessPath}
import fleet.shared.refined.Types.NonEmptyString

import cats.data.ValidatedNel
import com.monovore.decline.Argument
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.*

trait HealthConfigArgument:
  given livenessPathArgument: Argument[LivenessPath] = new Argument[LivenessPath]:
    override def read(string: String): ValidatedNel[String, LivenessPath] =
      string.refineValidatedNel[NonEmptyString].map(LivenessPath.apply)

    override def defaultMetavar: String = "url"

  given readinessPathArgument: Argument[ReadinessPath] = new Argument[ReadinessPath]:
    override def read(string: String): ValidatedNel[String, ReadinessPath] =
      string.refineValidatedNel[NonEmptyString].map(ReadinessPath.apply)

    override def defaultMetavar: String = "url"

object HealthConfigArgument extends HealthConfigArgument
