package es.eriktorr
package fleet.adapter.persistence.row

import fleet.domain.model.Driver

import doobie.Meta

import io.github.arainko.ducktape.*

final case class DriverRow(driverId: Long, driverName: String, licenseNumber: String)

object DriverRow:
  extension (driverRow: DriverRow)
    def toDriver: Driver = driverRow
      .into[Driver]
      .transform(Field.renamed(_.id, _.driverId), Field.renamed(_.name, _.driverName))

  given driverRowMeta: Meta[DriverRow] = ???
