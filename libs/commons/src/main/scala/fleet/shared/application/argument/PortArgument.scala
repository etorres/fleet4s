package es.eriktorr
package fleet.shared.application.argument

import cats.data.ValidatedNel
import cats.implicits.catsSyntaxValidatedId
import com.comcast.ip4s.Port
import com.monovore.decline.Argument

trait PortArgument:
  given portArgument: Argument[Port] = new Argument[Port]:
    override def read(string: String): ValidatedNel[String, Port] =
      Port.fromString(string).fold(s"Invalid port: $string".invalidNel)(_.validNel)
    override def defaultMetavar: String = "port"

object PortArgument extends PortArgument
