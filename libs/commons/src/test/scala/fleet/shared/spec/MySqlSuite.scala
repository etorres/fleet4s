package es.eriktorr
package fleet.shared.spec

import munit.{CatsEffectSuite, ScalaCheckEffectSuite}
import org.scalacheck.Test

trait MySqlSuite extends CatsEffectSuite with ScalaCheckEffectSuite:
  override def scalaCheckTestParameters: Test.Parameters =
    super.scalaCheckTestParameters.withMinSuccessfulTests(1).withWorkers(1)
