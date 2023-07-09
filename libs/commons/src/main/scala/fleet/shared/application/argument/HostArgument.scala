package es.eriktorr
package fleet.shared.application.argument

import cats.data.ValidatedNel
import cats.implicits.catsSyntaxValidatedId
import com.comcast.ip4s.Host
import com.monovore.decline.Argument

trait HostArgument:
  given hostArgument: Argument[Host] = new Argument[Host]:
    override def read(string: String): ValidatedNel[String, Host] =
      Host.fromString(string).fold(s"Invalid host: $string".invalidNel)(_.validNel)
    override def defaultMetavar: String = "host"

object HostArgument extends HostArgument
