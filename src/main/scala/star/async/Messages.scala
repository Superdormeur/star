package star.async

sealed trait Command
import akka.actor.typed.ActorRef

// These messages are used for the scheduling of tasks
final case class ScheduleTick() extends Command
final case class UnblockTick() extends Command

// The messages are used to handle the ShutDown of the application
final case class GracefulShutdown(ref: ActorRef[Command]) extends Command
final case class Terminated(name: String, ref: ActorRef[Command]) extends Command
