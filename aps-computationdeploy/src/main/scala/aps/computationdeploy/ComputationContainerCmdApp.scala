package aps.computationdeploy

import csw.framework.deploy.containercmd.ContainerCmd
import csw.prefix.models.Subsystem

object ComputationContainerCmdApp extends App {

  ContainerCmd.start("computation_container_cmd_app", Subsystem.withNameInsensitive("APS"), args)

}
