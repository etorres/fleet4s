package es.eriktorr
package fleet.application

import fleet.shared.Secret
import fleet.shared.application.JdbcConfig.{ConnectUrl, Password, Username}
import fleet.shared.application.{HttpServerConfig, JdbcConfig}

import cats.collections.Range
import cats.implicits.catsSyntaxEitherId
import com.comcast.ip4s.{host, port}
import com.monovore.decline.{Command, Help}
import io.github.iltotore.iron.refine
import munit.FunSuite

import scala.util.Properties

final class FleetControlConfigSuite extends FunSuite:
  test("should load configuration from environment variables"):
    assume(Properties.envOrNone("SBT_TEST_ENV_VARS").nonEmpty, "this test runs only on sbt")
    assertEquals(
      Command(name = "name", header = "header")(FleetControlConfig.opts)
        .parse(List.empty, sys.env),
      FleetControlConfig(
        HttpServerConfig(host"localhost", port"8000"),
        JdbcConfig.postgresql(
          Range(2, 4),
          ConnectUrl("jdbc:postgresql://localhost:3306/database_name".refine),
          Secret(Password("database_password".refine)),
          Username("database_username".refine),
        ),
      ).asRight[Help],
    )
