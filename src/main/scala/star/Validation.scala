package star

import play.api.libs.json.{Json, Reads}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object Validation {

  def validateJson[T](jsonString: String)(implicit jsonReads: Reads[T]): Future[T] = {
    Try(Json.parse(jsonString).as[T]) match {
      case Success(value) => Future.successful(value)
      case Failure(e)     => Future.failed(e)
    }
  }
}
