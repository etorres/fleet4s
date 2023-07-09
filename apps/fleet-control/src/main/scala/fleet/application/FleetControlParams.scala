package es.eriktorr
package fleet.application

import com.monovore.decline.Opts

final case class FleetControlParams(verbose: Boolean)

object FleetControlParams:
  def opts: Opts[FleetControlParams] = Opts
    .flag("verbose", short = "v", help = "Print extra metadata to the logs.")
    .orFalse
    .map(FleetControlParams.apply)
