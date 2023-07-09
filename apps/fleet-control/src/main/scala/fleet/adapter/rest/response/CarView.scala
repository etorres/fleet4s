package es.eriktorr
package fleet.adapter.rest.response

import fleet.domain.model.Car

import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import io.github.arainko.ducktape.*

final case class CarView(
    id: Long,
    model: String,
    color: String,
    chassisNumber: String,
    branch: String,
    company: String,
)

object CarView:
  def from(car: Car): CarView = car
    .into[CarView]
    .transform(
      Field.computed(_.branch, x => x.branch.name),
      Field.computed(_.company, x => x.branch.company.name),
    )

  given carViewEncoder: Encoder[CarView] = (view: CarView) =>
    Json.obj(
      ("id", view.id.asJson),
      ("model", view.model.asJson),
      ("color", view.color.asJson),
      ("chassis_number", view.chassisNumber.asJson),
      ("branch", view.branch.asJson),
      ("company", view.company.asJson),
    )
