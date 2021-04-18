package star.async

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

/**
  * When a schedule message arrives, we begin the process of the segments and mark the actor as blocked
  * This actor will be blocked until previous schedule completes and all messages that arrive
  * during blocked period are dropped
  *
  * @param actorKey
  * @param interval
  * @param context
  * @param timer
  */
abstract class FutureBasedActor(
    actorKey: String,
    interval: FiniteDuration,
    context: ActorContext[Command],
    timer: TimerScheduler[Command]
) extends StrictLogging {

  implicit val executionContext: ExecutionContext = context.executionContext

  /**
    * Action that will be performed every time this scheduler run
    */
  protected def doScheduledAction(): Future[_]

  /**
    * Finish the setup of the scheduler. Feed it to `Behaviors.setup` to start it.
    */
  def prepareSchedule(): Behavior[Command] = {
    Behaviors.receiveMessage[Command] { _ =>
      timer.startTimerAtFixedRate(actorKey, new ScheduleTick, interval)
      receive()
    }
  }

  private def receive(stopped: Boolean = false, actorBlocked: Boolean = false): Behavior[Command] = {
    Behaviors.receiveMessage {
      case ScheduleTick() =>
        if (stopped) {
          Behaviors.stopped
        } else if (actorBlocked) {
          Behaviors.same
        } else {
          logger.info(s"Starting Job for actor $actorKey")
          context.pipeToSelf(doScheduledAction()) { _ =>
            // map the Future value to a message, handled by this actor
            UnblockTick()
          }
          receive(stopped, actorBlocked = true)
        }
      case UnblockTick() =>
        logger.info(s"Finished Job for actor $actorKey")
        receive()
      case GracefulShutdown(_) => {
        if (!actorBlocked) {
          Behaviors.stopped
        }
        receive(stopped = true, actorBlocked = actorBlocked)
      }
    }
  }
}
