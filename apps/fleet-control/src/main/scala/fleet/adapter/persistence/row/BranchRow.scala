package es.eriktorr
package fleet.adapter.persistence.row

import fleet.domain.model.{Branch, Company}

import doobie.Read
import io.github.arainko.ducktape.*

final case class BranchRow(branchId: Long, branchName: String, branchTimezone: String)

object BranchRow:
  extension (branchRow: BranchRow)
    def toBranch(company: Company): Branch =
      branchRow
        .into[Branch]
        .transform(
          Field.renamed(_.id, _.branchId),
          Field.renamed(_.name, _.branchName),
          Field.renamed(_.timezone, _.branchTimezone),
          Field.const(_.company, company),
        )

  given branchRowRead: Read[BranchRow] = Read[(Long, String, String)].map {
    case (id, name, timezone) => BranchRow(id, name, timezone)
  }
