package star.models.db

import com.github.tminglei.slickpg.ExPostgresProfile

trait DbDriver extends ExPostgresProfile {
  object ExtendedApi extends API
  override val api: API = ExtendedApi
}

object DbDriver extends DbDriver
