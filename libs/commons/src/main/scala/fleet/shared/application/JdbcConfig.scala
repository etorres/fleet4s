package es.eriktorr
package fleet.shared.application

import fleet.shared.Secret
import fleet.shared.application.JdbcConfig.{ConnectUrl, DriverClassName, Password, Username}
import fleet.shared.refined.Types.{JdbcUrl, NonEmptyString}

import cats.Show
import cats.collections.Range
import io.github.iltotore.iron.*

final case class JdbcConfig(
    connections: Range[Int],
    connectUrl: ConnectUrl,
    driverClassName: DriverClassName,
    password: Secret[Password],
    username: Username,
)

object JdbcConfig:
  opaque type ConnectUrl <: String :| JdbcUrl = String :| JdbcUrl
  object ConnectUrl:
    def apply(x: String :| JdbcUrl): ConnectUrl = x

  opaque type DriverClassName = String :| NonEmptyString
  object DriverClassName extends RefinedTypeOps[DriverClassName]

  opaque type Password <: String :| NonEmptyString = String :| NonEmptyString
  object Password:
    def apply(x: String :| NonEmptyString): Password = x
    given Show[Password] = Show.fromToString

  opaque type Username <: String :| NonEmptyString = String :| NonEmptyString
  object Username:
    def apply(x: String :| NonEmptyString): Username = x

  def mysql(
      connections: Range[Int],
      connectUrl: ConnectUrl,
      password: Secret[Password],
      username: Username,
  ): JdbcConfig = JdbcConfig(
    connections,
    connectUrl,
    DriverClassName.applyUnsafe("com.mysql.cj.jdbc.Driver"),
    password,
    username,
  )

  given Show[JdbcConfig] =
    import scala.language.unsafeNulls
    Show.show(config => s"""{
                           |connections: ${config.connections.start}-${config.connections.end},
                           |connect-url: ${config.connectUrl},
                           |driver-class-name: ${config.driverClassName},
                           |password: ${config.password},
                           |username: ${config.username}
                           |}""".stripMargin.replaceAll("\\R", ""))
