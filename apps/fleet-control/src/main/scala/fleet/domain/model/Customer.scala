package es.eriktorr
package fleet.domain.model

import java.time.LocalDate

final case class Customer(id: Long, name: String, birthdate: LocalDate)
