package star.async

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, DispatcherSelector}
import com.typesafe.scalalogging.StrictLogging
import star.InsertController
import star.postgres.PostGreStorage

object SchedulerSystem extends StrictLogging {

  private val propsInserter = DispatcherSelector.fromConfig("scheduler.insert-dispatcher")

  def apply(insertController: InsertController, logStorage: PostGreStorage)(
      implicit actorSystem: ActorSystem
  ): Behavior[Command] = {

    Behaviors.setup { context =>
      val inserter = context.spawn(
        DataInsertorActor(insertController, logStorage),
        "DataInserter",
        propsInserter
      )
      val allActors: Set[ActorRef[Command]] = Set(inserter)

      allActors.foreach(actor => context.watchWith(actor, Terminated(actor.path.name, actor)))

      behavior(inserter)
    }
  }

  /** Main behavior of the scheduler :
    *
    *  GracefulShutdown : handle the shutdown of the application
    *  ScheduleTick : message to start the scheduling lifecycle of children actors
    */
  def behavior(actor: ActorRef[Command]): Behavior[Command] = {
    Behaviors.receiveMessage {
      case GracefulShutdown(ref) =>
        actor ! GracefulShutdown(ref)
        stopping(ref)
      case ScheduleTick() =>
        actor ! ScheduleTick()
        Behaviors.same
    }
  }

  /** Stopping behavior of the scheduler :
    *
    *  Terminated(name, actorRef) : message coming from children actor that just stopped itself.
    *
    * @param ref : Reference of the main actor, to tell him when all actors were stopped.
    */
  def stopping(ref: ActorRef[Command]): Behavior[Command] = {
    Behaviors.receive({
      case (ctx, Terminated(name, _)) =>
        logger.info(s"Terminated actor $name")
        logger.info("Shutdown scheduler ...")
        ref ! Terminated("scheduler", ctx.self)
        Behaviors.stopped
    })
  }
}
