package es.eriktorr
package fleet.shared.refined

import io.github.iltotore.iron.DescribedAs
import io.github.iltotore.iron.constraint.any.Not
import io.github.iltotore.iron.constraint.string.{Blank, Match, StartWith}

object Types:
  type JdbcUrl = DescribedAs[StartWith["jdbc:"], "Should start with jdbc:"]

  type NonEmptyString =
    DescribedAs[Not[Blank], "Should contain at least one non-whitespace character"]

  type UrlPathSegment = DescribedAs[Match["^/[0-9a-zA-Z_-]+"], "Should be a valid URL path segment"]
