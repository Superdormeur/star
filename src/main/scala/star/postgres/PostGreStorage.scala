package star.postgres

import com.typesafe.scalalogging.StrictLogging
import star.dto.StatRow
import star.models.dao.StatTable
import star.models.db
import star.models.db.DbDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PostGreStorage(database: db.DbDriver.backend.Database) extends StrictLogging {

  def upsertSegmentValues(segmentValues: Seq[StatRow]): Future[Unit] = {
    val action = StatTable.upsertAll(segmentValues).transactionally

    database.run(action).recoverWith {
      case err =>
        val errMessage = s"Could not upsert segments"
        logAndFail(errMessage, err)
    }
  }

  private def logAndFail[A](errMessage: String, err: Throwable): Future[A] = {
    logger.error(errMessage, err.getStackTrace)
    Future.failed(new Exception(s"$errMessage -- ${err.getMessage} -- ${err.getLocalizedMessage}"))
  }
}
