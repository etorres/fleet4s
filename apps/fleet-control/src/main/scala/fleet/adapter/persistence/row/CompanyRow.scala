package es.eriktorr
package fleet.adapter.persistence.row

import fleet.domain.model.Company

import doobie.Read
import io.github.arainko.ducktape.*

final case class CompanyRow(companyId: Long, companyName: String, companyTimezone: String)

object CompanyRow:
  extension (companyRow: CompanyRow)
    def toCompany: Company =
      companyRow
        .into[Company]
        .transform(
          Field.renamed(_.id, _.companyId),
          Field.renamed(_.name, _.companyName),
          Field.renamed(_.timezone, _.companyTimezone),
        )

  given companyRowRead: Read[CompanyRow] = Read[(Long, String, String)].map {
    case (id, name, timezone) => CompanyRow(id, name, timezone)
  }
