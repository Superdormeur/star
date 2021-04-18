package star

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import com.typesafe.scalalogging.StrictLogging
import star.dto.SessionPayload

import java.util.Collections
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class InsertController extends StrictLogging {

  val sessionArray: java.util.List[SessionPayload] =
    Collections.synchronizedList(new java.util.ArrayList[SessionPayload])

  def savePayload(sessionPayload: SessionPayload): Future[HttpResponse] = {
    Future {
      sessionArray.add(sessionPayload)
      HttpResponse(StatusCodes.OK)
    }.recoverWith {
      case _ =>
        Future.successful {
          HttpResponse(
            status = StatusCodes.InternalServerError,
            entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Something went wrong when saving the payload")
          )
        }
    }
  }
}
