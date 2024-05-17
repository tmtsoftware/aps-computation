lazy val aggregatedProjects: Seq[ProjectReference] = Seq(
  `aps-computationassembly`,
  `aps-computationhcd`,
  `aps-computationdeploy`,
  `aps-computationclient`
)

lazy val `computation-root` = project
  .in(file("."))
  .aggregate(aggregatedProjects: _*)

// assembly module
lazy val `aps-computationassembly` = project
  .settings(
    libraryDependencies ++= Dependencies.Computationassembly
  )

// hcd module
lazy val `aps-computationhcd` = project
  .settings(
    libraryDependencies ++= Dependencies.Computationhcd
  )

// hcd module
lazy val `aps-computationclient` = project
  .settings(
    libraryDependencies ++= Dependencies.Computationclient
  )

// deploy module
lazy val `aps-computationdeploy` = project
  .dependsOn(
    `aps-computationassembly`,
    `aps-computationhcd`
  )
  .enablePlugins(CswBuildInfo)
  .settings(
    libraryDependencies ++= Dependencies.ComputationDeploy
  )
