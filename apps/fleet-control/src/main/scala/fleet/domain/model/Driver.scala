package es.eriktorr
package fleet.domain.model

final case class Driver(id: Long, name: String, licenseNumber: String, trips: List[Trip])
