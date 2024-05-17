import sbt._

object Dependencies {

  val Computationassembly = Seq(
    CSW.`csw-framework`,
    CSW.`csw-testkit` % Test,
    Libs.`scalatest` % Test,
    Libs.`junit-4-13` % Test,
  )

  val Computationhcd = Seq(
    CSW.`csw-framework`,
    CSW.`csw-testkit` % Test,
    Libs.`scalatest` % Test,
    Libs.`junit-4-13` % Test,
  )

  val ComputationDeploy = Seq(
    CSW.`csw-framework`,
    CSW.`csw-testkit` % Test
  )

  val Computationclient = Seq(
    CSW.`csw-framework`,
    CSW.`csw-testkit` % Test
  )

}
