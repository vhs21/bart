package repositories.impl

import anorm._
import models.Model
import play.api.db.{DBApi, Database}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

abstract class ModelRepository[A <: Model](val dbapi: DBApi)(implicit ec: ExecutionContext) {

  protected val db: Database = dbapi.database("default")

  def selectAll(query: SimpleSql[Row], parser: RowParser[A]): Future[Seq[A]] = Future {
    db.withConnection { implicit connection => query.as(parser *) }
  }(ec)

  def select(id: Long, query: SimpleSql[Row], parser: RowParser[A]): Future[Option[A]] = Future {
    db.withConnection { implicit connection => query.as(parser.singleOpt) }
  }(ec)

  def insert(element: A, query: SimpleSql[Row]): Future[Option[Long]] = Future {
    db.withConnection { implicit connection => query.executeInsert() }
  }(ec)

  def delete(id: Long, query: SimpleSql[Row]): Future[Int] = Future {
    db.withConnection { implicit connection => query.executeUpdate() }
  }(ec)

  def update(id: Long, element: A, query: SimpleSql[Row]): Future[Int] = Future {
    db.withConnection { implicit connection => query.executeUpdate() }
  }(ec)

}
