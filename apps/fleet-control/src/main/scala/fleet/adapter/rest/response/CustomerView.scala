package es.eriktorr
package fleet.adapter.rest.response

import fleet.domain.model.Customer

import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import io.github.arainko.ducktape.to

import java.time.LocalDate

final case class CustomerView(id: Long, name: String, birthdate: LocalDate)

object CustomerView:
  def from(customer: Customer): CustomerView = customer.to[CustomerView]

  given customerViewEncoder: Encoder[CustomerView] = (view: CustomerView) =>
    Json.obj(
      ("id", view.id.asJson),
      ("name", view.name.asJson),
      ("birthdate", view.birthdate.asJson),
    )
