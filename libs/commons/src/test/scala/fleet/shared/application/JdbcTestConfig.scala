package es.eriktorr
package fleet.shared.application

import fleet.shared.Secret
import fleet.shared.application.JdbcConfig.{ConnectUrl, Password, Username}

import cats.collections.Range
import io.github.iltotore.iron.*

enum JdbcTestConfig(val config: JdbcConfig, val database: String):
  case FleetDatabase
      extends JdbcTestConfig(
        JdbcConfig.mysql(
          connections = Range(1, 3),
          connectUrl = ConnectUrl(
            s"jdbc:mysql://${JdbcTestConfig.connectionHost}/${JdbcTestConfig.fleetDatabase}?${JdbcTestConfig.connectionCommonParams}".refine,
          ),
          password = Secret(Password("changeMe".refine)),
          username = Username("test".refine),
        ),
        JdbcTestConfig.fleetDatabase,
      )

/** Test JDBC connection configuration.
  * @see
  *   [[https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-time-instants.html Preserving Time Instants]]
  */
object JdbcTestConfig:
  final private val connectionHost = "mysql.test:3306"
  final private val connectionCommonParams =
    "useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&preserveInstants=true&connectionTimeZone=Europe/Madrid"
  final private val fleetDatabase = "fleet"
