package star.models.db

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult
import star.Configuration
import star.models.db
import star.models.db.DbDriver.api._

object Db {

  lazy val database: db.DbDriver.backend.Database = Database.forConfig("db")

  // Applies all pending migrations
  def migrateDb(): MigrateResult = {
    Flyway
      .configure()
      .dataSource(
        Configuration.Postgres.dbUrl,
        Configuration.Postgres.dbUser,
        Configuration.Postgres.dbPassword
      )
      .load()
      .migrate()
  }
}
