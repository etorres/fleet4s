package es.eriktorr
package fleet.domain.service

import fleet.domain.model.Trip

import cats.effect.IO

import java.time.ZonedDateTime

final class TripService(tripRepository: TripRepository):
  def findAll(): IO[List[Trip]] = tripRepository.findAll()

  def findAllByStartOnIsBetween(from: ZonedDateTime, to: ZonedDateTime): IO[List[Trip]] =
    tripRepository.findAllByStartOnIsBetween(from, to)
