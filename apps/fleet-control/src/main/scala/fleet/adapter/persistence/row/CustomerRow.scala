package es.eriktorr
package fleet.adapter.persistence.row

import fleet.domain.model.Customer
import fleet.shared.adapter.persistence.TemporalMapper.localDateMeta

import doobie.Read
import io.github.arainko.ducktape.*

import java.time.LocalDate

final case class CustomerRow(customerId: Long, customerName: String, birthdate: LocalDate)

object CustomerRow:
  extension (customerRow: CustomerRow)
    def toCustomer: Customer = customerRow
      .into[Customer]
      .transform(Field.renamed(_.id, _.customerId), Field.renamed(_.name, _.customerName))

  given customerRowRead: Read[CustomerRow] = Read[(Long, String, LocalDate)].map {
    case (id, name, birthdate) => CustomerRow(id, name, birthdate)
  }
