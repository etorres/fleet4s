package es.eriktorr
package fleet.domain.model

final case class Car(
    id: Long,
    model: String,
    chassisNumber: String,
    color: String,
    registrationNumber: String,
    branch: Branch,
)
