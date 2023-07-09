package es.eriktorr
package fleet.domain.service

import fleet.domain.model.Trip

import cats.effect.IO

import java.time.ZonedDateTime

trait TripRepository:
  def listAll(): IO[List[Trip]]

  def findAllByStartOnIsBetween(from: ZonedDateTime, to: ZonedDateTime): IO[List[Trip]]
