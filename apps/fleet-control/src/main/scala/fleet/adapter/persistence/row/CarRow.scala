package es.eriktorr
package fleet.adapter.persistence.row

import fleet.domain.model.{Branch, Car}

import doobie.Meta
import io.github.arainko.ducktape.*

final case class CarRow(
    carId: Long,
    model: String,
    chassisNumber: String,
    color: String,
    registrationNumber: String,
)

object CarRow:
  extension (carRow: CarRow)
    def toCar(branch: Branch): Car =
      carRow.into[Car].transform(Field.renamed(_.id, _.carId), Field.const(_.branch, branch))

  given carRowMeta: Meta[CarRow] = ???
