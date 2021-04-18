package star.dto

import org.joda.time.DateTime
import play.api.libs.json._

case class SessionPayload(
    token: String,
    customer: String,
    content: String,
    timespan: Int,
    p2p: Int,
    cdn: Int,
    sessionDuration: Int
)

object SessionPayload {
  implicit val read: Format[SessionPayload] = Json.format[SessionPayload]
}

case class StatRow(
    id: Option[Int],
    Time: DateTime,
    customer: String,
    content: String,
    cdn: Int,
    p2p: Int
)

object StatRow {

  def fromStat(dateTime: DateTime, customer: String, content: String, cdn: Int, p2p: Int): StatRow = {
    StatRow(
      id = None,
      Time = dateTime,
      customer = customer,
      content = content,
      cdn = cdn,
      p2p = p2p
    )
  }
}
