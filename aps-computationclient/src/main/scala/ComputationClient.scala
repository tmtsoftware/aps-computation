package aps.computationclient

import java.net.InetAddress

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorSystem, Behavior, SpawnProtocol}
import akka.util.Timeout
import csw.command.api.scaladsl.CommandService
import csw.command.client.CommandServiceFactory
import csw.location.api.models.ComponentType.Assembly
import csw.location.api.models.Connection.AkkaConnection
import csw.location.api.models._
import csw.location.client.scaladsl.HttpLocationServiceFactory
import csw.logging.client.commons.AkkaTypedExtension.UserActorFactory
import csw.logging.client.scaladsl.{GenericLoggerFactory, LoggingSystemFactory}
import csw.params.commands.{CommandName, Setup}
import csw.params.core.generics.{Key, KeyType}
import csw.prefix.models.Prefix

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * A client to test locating and communicating with the Galil assembly
 */
object ComputationClient extends App {

  implicit val typedSystem: ActorSystem[SpawnProtocol.Command] = ActorSystem(SpawnProtocol(), "ComputationClient")
  implicit lazy val ec: ExecutionContextExecutor               = typedSystem.executionContext
  implicit val timeout: Timeout                                = Timeout(3.seconds)

  private val locationService = HttpLocationServiceFactory.makeLocalClient
  private val host            = InetAddress.getLocalHost.getHostName
  LoggingSystemFactory.start("ComputationClientApp", "0.1", host, typedSystem)

  private val log = GenericLoggerFactory.getLogger
  log.info("Starting ComputationClient")

  typedSystem.spawn(initialBehavior, "ComputationClient")

  def initialBehavior: Behavior[TrackingEvent] =
    Behaviors.setup { ctx =>
      val connection = AkkaConnection(ComponentId(Prefix("APS.computationAssembly"), Assembly))
      locationService.subscribe(
        connection,
        { loc =>
          ctx.self ! loc
        }
      )
      subscriberBehavior
    }

  def subscriberBehavior: Behavior[TrackingEvent] = {
    Behaviors.receive[TrackingEvent] { (ctx, msg) =>
      msg match {
        case LocationUpdated(loc) =>
          log.info(s"LocationUpdated: $loc")
          interact(ctx, CommandServiceFactory.make(loc.asInstanceOf[AkkaLocation])(ctx.system))
        case LocationRemoved(loc) =>
          log.info(s"LocationRemoved: $loc")
      }
      Behaviors.same
    } receiveSignal { case (ctx, x) =>
      log.info(s"${ctx.self} received signal $x")
      Behaviors.stopped
    }
  }

  private def interact(ctx: ActorContext[TrackingEvent], assembly: CommandService): Unit = {
    val maybeObsId = None

    val axisKey: Key[Char]  = KeyType.CharKey.make("axis")
    val countsKey: Key[Int] = KeyType.IntKey.make("counts")

    val setup = Setup(Prefix("csw.test.client"), CommandName("setRelTarget"), maybeObsId)
      .add(axisKey.set('A'))
      .add(countsKey.set(2))

    assembly.submit(setup).onComplete {
      case Success(resp) =>
        log.info(s"HCD responded with $resp")
      case Failure(ex) =>
        ex.printStackTrace()
        log.error("Failed to send command to GalilHcd", ex = ex)
    }
  }
}
