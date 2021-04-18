package star.dto

import org.joda.time.DateTime
import play.api.libs.json._

case class SessionPayload(
    token: String,
    customer: String,
    content: String,
    timespan: Long,
    p2p: Long,
    cdn: Long,
    sessionDuration: Long
)

object SessionPayload {
  implicit val read: Format[SessionPayload] = Json.format[SessionPayload]
}

case class StatRow(
    id: Option[Int],
    Time: DateTime,
    customer: String,
    content: String,
    cdn: Long,
    p2p: Long
)

object StatRow {

  def fromStat(dateTime: DateTime, customer: String, content: String, cdn: Long, p2p: Long): StatRow = {
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
