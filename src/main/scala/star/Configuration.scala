package star

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.{DurationInt, FiniteDuration}

object Configuration {

  private val applicationConfig = ConfigFactory.load()

  private val wsConfig: Config = applicationConfig.getConfig("star")

  val host: String = wsConfig.getString("host")
  val port: Int = wsConfig.getInt("port")

  val gracefulShutDownTimeout: Int = wsConfig.getInt("graceful-shutdown-timeout-akka")
  val shutDownTimeout: Int = wsConfig.getInt("shutdown-timeout")

  object Scheduler {
    private val schedulerConfig = applicationConfig.getConfig("scheduler")
    val insertInterval: FiniteDuration = schedulerConfig.getInt("insert-interval").milliseconds
  }

  object Postgres {
    private val dbConf: Config = applicationConfig.getConfig("db")
    private val dbProperties = dbConf.getConfig("properties")

    val dbHost: String = dbProperties.getString("serverName")
    private val dbPort: Int = dbProperties.getInt("portNumber")
    private val dbName: String = dbProperties.getString("databaseName")
    val dbUser: String = dbProperties.getString("user")
    val dbPassword: String = dbProperties.getString("password")

    val dbUrl: String = s"jdbc:postgresql://$dbHost:$dbPort/$dbName"
  }
}
