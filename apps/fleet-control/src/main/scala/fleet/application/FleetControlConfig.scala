package es.eriktorr
package fleet.application

import fleet.shared.Secret
import fleet.shared.application.HealthConfig.{LivenessPath, ReadinessPath}
import fleet.shared.application.JdbcConfig.{ConnectUrl, Password, Username}
import fleet.shared.application.argument.HealthConfigArgument.{
  livenessPathArgument,
  readinessPathArgument,
}
import fleet.shared.application.argument.HostArgument.hostArgument
import fleet.shared.application.argument.JdbcConfigArgument.{
  connectUrlArgument,
  passwordArgument,
  usernameArgument,
}
import fleet.shared.application.argument.PortArgument.portArgument
import fleet.shared.application.argument.RangeArgument.intRangeArgument
import fleet.shared.application.{HealthConfig, HttpServerConfig, JdbcConfig}

import cats.Show
import cats.collections.Range
import cats.implicits.{
  catsSyntaxTuple2Semigroupal,
  catsSyntaxTuple3Semigroupal,
  catsSyntaxTuple4Semigroupal,
  showInterpolator,
}
import com.comcast.ip4s.{Host, Port}
import com.monovore.decline.Opts

final case class FleetControlConfig(
    healthConfig: HealthConfig,
    httpServerConfig: HttpServerConfig,
    jdbcConfig: JdbcConfig,
)

object FleetControlConfig:
  given Show[FleetControlConfig] =
    import scala.language.unsafeNulls
    Show.show(config => show"""{
                              |http-server: ${config.httpServerConfig},
                              |jdbc: ${config.jdbcConfig}
                              |}""".stripMargin.replaceAll("\\R", ""))

  def opts: Opts[FleetControlConfig] =
    val healthConfig = (
      Opts
        .env[LivenessPath](name = "FLEET4S_HEALTH_LIVENESS_PATH", help = "Set liveness path.")
        .withDefault(HealthConfig.defaultLivenessPath),
      Opts
        .env[Port](name = "FLEET4S_HEALTH_PORT", help = "Set health port.")
        .withDefault(HealthConfig.defaultPort),
      Opts
        .env[ReadinessPath](name = "FLEET4S_HEALTH_READINESS_PATH", help = "Set readiness path.")
        .withDefault(HealthConfig.defaultReadinessPath),
    ).mapN(HealthConfig.apply)

    val httpServerConfig = (
      Opts
        .env[Host](name = "FLEET4S_HTTP_HOST", help = "Set HTTP host.")
        .withDefault(HttpServerConfig.defaultHost),
      Opts
        .env[Port](name = "FLEET4S_HTTP_PORT", help = "Set HTTP port.")
        .withDefault(HttpServerConfig.defaultPort),
    ).mapN(HttpServerConfig.apply)

    val jdbcConfig =
      (
        Opts
          .env[Range[Int]](
            name = "FLEET4S_JDBC_CONNECTIONS",
            help = "Set JDBC Connections.",
          )
          .validate("Must be between 1 and 16")(_.overlaps(Range(1, 16)))
          .withDefault(Range(1, 3)),
        Opts.env[ConnectUrl](
          name = "FLEET4S_JDBC_CONNECT_URL",
          help = "Set JDBC Connect URL.",
        ),
        Opts
          .env[Password](
            name = "FLEET4S_JDBC_PASSWORD",
            help = "Set JDBC Password.",
          )
          .map(Secret.apply[Password]),
        Opts.env[Username](
          name = "FLEET4S_JDBC_USERNAME",
          help = "Set JDBC Username.",
        ),
      ).mapN(JdbcConfig.mysql)

    (healthConfig, httpServerConfig, jdbcConfig).mapN(FleetControlConfig.apply)
