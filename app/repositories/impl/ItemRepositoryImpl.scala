package repositories.impl

import anorm._
import com.google.inject.Inject
import models.Item
import play.api.db.DBApi
import repositories.ItemRepository
import utils.ItemSearchCriteria

import scala.concurrent.{ExecutionContext, Future}

class ItemRepositoryImpl @Inject()(dbapi: DBApi)(implicit val ec: ExecutionContext)
  extends ModelRepository[Item](dbapi, Item.parser) with ItemRepository {

  override def selectAll: Future[Seq[Item]] = Future.successful(Seq.empty)

  override def select(id: Long): Future[Option[Item]] = select(
    SQL"""SELECT items.id_item, items.name, items.description, items.registration_date, items.id_user, items.id_item_status
          FROM items
          WHERE items.id_item = $id""")

  override def insert(element: Item): Future[Option[Long]] = insert(
    SQL"""INSERT INTO items (name, description, id_user) VALUES(
          ${element.name},
          ${element.description},
          ${element.idUser})""")

  override def delete(id: Long): Future[Option[Int]] = Future.successful(None)

  override def update(id: Long, element: Item): Future[Option[Int]] = update(
    SQL"""UPDATE items
          SET name = ${element.name},
              description = ${element.description}
          WHERE id_item = $id""")

  override def count(itemSearchCriteria: ItemSearchCriteria): Future[Int] = Future {
    db.withConnection { implicit connection =>
      SQL("SELECT COUNT(*) FROM items" + itemSearchCriteria.whereClause)
        .on(itemSearchCriteria.namedWhereParamsList: _*)
        .as(SqlParser.int(1).single)
    }
  }

  override def selectAll(itemSearchCriteria: ItemSearchCriteria): Future[Seq[Item]] = selectAll(
    SQL(
      s"""SELECT items.id_item, items.name, items.description, items.registration_date, items.id_user, items.id_item_status
          FROM items
          ${itemSearchCriteria.whereClause}
          ORDER BY items.id_item DESC
          ${itemSearchCriteria.limitClause}""")
      .on(itemSearchCriteria.namedWhereParamsList: _*)
      .on(itemSearchCriteria.namedLimitParamsList: _*)
  )

  override def updateStatus(id: Long, idStatus: Int): Future[Int] = Future {
    db.withConnection { implicit connection =>
      SQL"""UPDATE items
            SET id_item_status = $idStatus
            WHERE id_item = $id""".executeUpdate()
    }
  }

}
