package es.eriktorr
package fleet.shared.application.argument

import fleet.shared.application.JdbcConfig.{ConnectUrl, Password, Username}
import fleet.shared.refined.Types.{JdbcUrl, NonEmptyString}

import cats.data.ValidatedNel
import com.monovore.decline.Argument
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.*

trait JdbcConfigArgument:
  given connectUrlArgument: Argument[ConnectUrl] = new Argument[ConnectUrl]:
    override def read(string: String): ValidatedNel[String, ConnectUrl] =
      string.refineValidatedNel[JdbcUrl].map(ConnectUrl.apply)

    override def defaultMetavar: String = "url"

  given passwordArgument: Argument[Password] = new Argument[Password]:
    override def read(string: String): ValidatedNel[String, Password] =
      string.refineValidatedNel[NonEmptyString].map(Password.apply)

    override def defaultMetavar: String = "password"

  given usernameArgument: Argument[Username] = new Argument[Username]:
    override def read(string: String): ValidatedNel[String, Username] =
      string.refineValidatedNel[NonEmptyString].map(Username.apply)

    override def defaultMetavar: String = "username"

object JdbcConfigArgument extends JdbcConfigArgument
