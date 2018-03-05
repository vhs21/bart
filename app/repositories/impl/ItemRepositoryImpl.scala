package repositories.impl

import anorm._
import com.google.inject.Inject
import models.Item
import play.api.db.DBApi
import repositories.ItemRepository

import scala.concurrent.{ExecutionContext, Future}

class ItemRepositoryImpl @Inject()(dbapi: DBApi)(implicit ec: ExecutionContext)
  extends ModelRepository[Item](dbapi) with ItemRepository {

  override def selectAll: Future[Seq[Item]] = selectAll(
    SQL"""SELECT items.id_item, items.name, items.description, items.id_user, items.registration_date, items.id_item_status
          FROM items""", Item.parser)

  override def select(id: Long): Future[Option[Item]] = select(id,
    SQL"""SELECT items.id_item, items.name, items.description, items.id_user, items.registration_date, items.id_item_status
          FROM items
          WHERE items.id_item = $id""", Item.parser)

  override def insert(element: Item): Future[Option[Long]] = insert(element,
    SQL"""INSERT INTO items (name, description, id_user) VALUES(
          ${element.name},
          ${element.description},
          ${element.idUser}})""")

  override def delete(id: Long): Future[Int] = delete(id,
    SQL"""DELETE FROM items
          WHERE item_id = $id""")

  override def update(id: Long, element: Item): Future[Int] = update(id, element,
    SQL"""UPDATE items
          SET name = ${element.name},
              description = ${element.description}
          WHERE id_item = $id""")

  override def updateStatus(id: Long, idStatus: Int): Future[Int] = Future {
    db.withConnection { implicit connection =>
      SQL"""UPDATE items
            SET id_item_status = $idStatus
            WHERE id_item = $id""".executeUpdate()
    }
  }
}
