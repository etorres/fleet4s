package es.eriktorr
package fleet.shared.application

import cats.Show
import com.comcast.ip4s.{ipv4, port, Host, Port}

final case class HttpServerConfig(host: Host, port: Port)

object HttpServerConfig:
  val defaultHost: Host = ipv4"0.0.0.0"
  val defaultPort: Port = port"8080"

  val default: HttpServerConfig = HttpServerConfig(defaultHost, defaultPort)

  given Show[HttpServerConfig] =
    import scala.language.unsafeNulls
    Show.show(config => s"""{
                           |host: ${config.host},
                           |port: ${config.port}
                           |}""".stripMargin.replaceAll("\\R", ""))
