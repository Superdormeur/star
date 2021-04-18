package star.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.server.Route
import star.dto.SessionPayload
import star.{InsertController, Validation}

import scala.util.{Failure, Success}

class StarRoutes(controller: InsertController) {

  val routesPost: Route = post {
    path("stats") {
      entity(as[String]) { jsonInput =>
        onComplete(Validation.validateJson[SessionPayload](jsonInput)) {
          case Success(payload) =>
            complete {
              controller.savePayload(payload)
            }
          case Failure(_) =>
            complete {
              HttpResponse(
                status = StatusCodes.BadRequest,
                entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Unexpected JSON content.")
              )
            }
        }
      }
    }
  }
}
