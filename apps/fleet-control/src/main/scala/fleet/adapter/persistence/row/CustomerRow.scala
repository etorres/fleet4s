package es.eriktorr
package fleet.adapter.persistence.row

import fleet.domain.model.Customer

import doobie.Meta
import io.github.arainko.ducktape.*

import java.time.LocalDate

final case class CustomerRow(customerId: Long, customerName: String, birthdate: LocalDate)

object CustomerRow:
  extension (customerRow: CustomerRow)
    def toCustomer: Customer = customerRow
      .into[Customer]
      .transform(Field.renamed(_.id, _.customerId), Field.renamed(_.name, _.customerName))

  given customerRowMeta: Meta[CustomerRow] = ???
