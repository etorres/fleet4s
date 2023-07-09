package es.eriktorr
package fleet.adapter.rest.response

import fleet.domain.model.Driver

import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import io.github.arainko.ducktape.to

final case class DriverView(id: Long, name: String, licenseNumber: String)

object DriverView:
  def from(driver: Driver): DriverView = driver.to[DriverView]

  given driverViewEncoder: Encoder[DriverView] = (view: DriverView) =>
    Json.obj(
      ("id", view.id.asJson),
      ("name", view.name.asJson),
      ("license_number", view.licenseNumber.asJson),
    )
