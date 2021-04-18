package star

import akka.actor.ActorSystem
import akka.actor.typed.Scheduler
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.Http
import akka.util.Timeout
import com.typesafe.scalalogging.StrictLogging
import star.async.{Command, GracefulShutdown, ScheduleTick, SchedulerSystem}
import star.models.db.Db
import star.postgres.PostGreStorage
import star.routes.StarRoutes

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object Main extends App with StrictLogging {

  implicit val system: ActorSystem = ActorSystem()
  private val controller = new InsertController()

  Db.migrateDb()
  private val logStorage = new PostGreStorage(Db.database)

  private val schedulerActor: akka.actor.typed.ActorSystem[Command] = {
    val schedulerActor: akka.actor.typed.ActorSystem[Command] =
      akka.actor.typed.ActorSystem(
        SchedulerSystem(controller, logStorage),
        "schedulerSystem"
      )
    schedulerActor ! ScheduleTick()
    schedulerActor
  }

  val routes = new StarRoutes(controller).routesPost

  val bindingFuture = Http().newServerAt(Configuration.host, Configuration.port).bindFlow(routes)

  logger.info(s"Server online at http://${Configuration.host}:${Configuration.port}/")

  sys.addShutdownHook({
    val unbindF = bindingFuture.flatMap(_.unbind())
    unbindF.onComplete {
      case Success(_)   => logger.info("Server was successfully unbound")
      case Failure(err) => logger.warn("Server was NOT unbound", err)
    }

    implicit val scheduler: Scheduler = schedulerActor.scheduler
    implicit val timeout: Timeout = Configuration.gracefulShutDownTimeout.seconds

    val terminateActors = schedulerActor.ask[Command](ref => GracefulShutdown(ref))
    terminateActors.onComplete {
      case Success(_)   => logger.info("Actor system was successfully terminated")
      case Failure(err) => logger.warn("Actor system was NOT terminated", err)
    }

    Await
      .ready(terminateActors, Configuration.shutDownTimeout.seconds)
      .flatMap(_ => unbindF)
      .onComplete { _ =>
        logger.debug("Shutdown procedure complete")
        system.terminate()
      }
  })
}
