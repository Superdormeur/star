package star.async

import akka.actor.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import com.typesafe.scalalogging.StrictLogging
import org.joda.time.{DateTime, DateTimeZone}
import star.dto.{SessionPayload, StatRow}
import star.postgres.PostGreStorage
import star.{Configuration, InsertController}

import scala.collection.JavaConverters._
import scala.concurrent.Future

object DataInsertorActor {

  def apply(insertController: InsertController, logStorage: PostGreStorage)(
      implicit actorSystem: ActorSystem
  ): Behavior[Command] = {
    Behaviors.setup { context =>
      Behaviors.withTimers(
        timers =>
          new DataInsertorActor(
            insertController,
            logStorage,
            context,
            timers
          ).prepareSchedule()
      )
    }
  }
}

class DataInsertorActor(
    insertController: InsertController,
    logStorage: PostGreStorage,
    context: ActorContext[Command],
    timer: TimerScheduler[Command]
)(implicit actorSystem: ActorSystem)
    extends FutureBasedActor(actorKey = "Insert", Configuration.Scheduler.insertInterval, context, timer)
    with StrictLogging {

  override protected def doScheduledAction(): Future[_] = {
    insertController.sessionArray.synchronized {
      val list: Seq[SessionPayload] = insertController.sessionArray.asScala
      if (list.nonEmpty) {
        val now = DateTime.now(DateTimeZone.UTC)

        val statRows = list
          .groupBy(x => (x.customer, x.content))
          .map {
            case ((customer, content), payloads) =>
              val (cdn, p2p) = sumPayloads(payloads)
              StatRow.fromStat(now, customer, content, cdn, p2p)
          }
          .toSeq

        logger.info(s"inserting ${statRows.size} lines into postgres")
        logStorage.upsertSegmentValues(statRows).map { _ =>
          insertController.sessionArray.clear()
        }
      } else {
        Future.successful(Unit)
      }
    }
  }

  private def sumPayloads(payloads: Seq[SessionPayload]): (Int, Int) = {
    payloads.foldLeft((0, 0)) { case ((cdnSum, p2pSum), next) => (cdnSum + next.cdn, p2pSum + next.p2p) }
  }
}
