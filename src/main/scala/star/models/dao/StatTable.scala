package star.models.dao

import com.github.tototoshi.slick.PostgresJodaSupport._
import org.joda.time.DateTime
import slick.lifted.ProvenShape
import star.dto.StatRow
import star.models.db.DbDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

class StatTable(tag: Tag) extends Table[StatRow](tag, "stats") {

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def time: Rep[DateTime] = column[DateTime]("time")

  def customer: Rep[String] = column[String]("customer")

  def content: Rep[String] = column[String]("content")

  def cdn: Rep[Long] = column[Long]("cdn")

  def p2p: Rep[Long] = column[Long]("p2p")

  def * : ProvenShape[StatRow] =
    (
      id.?,
      time,
      customer,
      content,
      cdn,
      p2p
    ) <> ((StatRow.apply _).tupled, StatRow.unapply)
}

object StatTable {

  // Low level actions, use and compose at your own risk for maximum flexibility

  val all = TableQuery[StatTable]

  def upsertAll(segmentValues: Seq[StatRow]): DBIO[Unit] = {
    (all ++= segmentValues).map(_ => ())
  }
}
