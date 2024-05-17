package aps.computationdeploy

import csw.framework.deploy.hostconfig.HostConfig
import csw.prefix.models.Subsystem

object ComputationHostConfigApp extends App {

  HostConfig.start("computation_host_config_app", Subsystem.withNameInsensitive("APS"), args)

}
