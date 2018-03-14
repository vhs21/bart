package repositories.impl

import anorm._
import play.api.db.{DBApi, Database}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

abstract class ModelRepository[A](val dbapi: DBApi, val parser: RowParser[A])(implicit ec: ExecutionContext) {

  protected val db: Database = dbapi.database("default")

  def selectAll(query: SimpleSql[Row]): Future[Seq[A]] = Future {
    db.withConnection { implicit connection => query.as(parser *) }
  }

  def select(query: SimpleSql[Row]): Future[Option[A]] = Future {
    db.withConnection { implicit connection => query.as(parser.singleOpt) }
  }

  def insert(query: SimpleSql[Row]): Future[Option[Long]] = Future {
    db.withConnection { implicit connection => query.executeInsert() }
  }

  def delete(query: SimpleSql[Row]): Future[Int] = Future {
    db.withConnection { implicit connection => query.executeUpdate() }
  }

  def update(query: SimpleSql[Row]): Future[Int] = Future {
    db.withConnection { implicit connection => query.executeUpdate() }
  }

}
