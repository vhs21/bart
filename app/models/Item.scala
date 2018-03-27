package models

import java.time.LocalDateTime

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import models.ItemStatus.ItemStatus
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Item(
                 id: Option[Long] = None,
                 name: String,
                 description: Option[String] = None,
                 registrationDate: Option[LocalDateTime] = None,
                 idUser: Option[Long] = None,
                 itemStatus: Option[ItemStatus] = None)

object Item {

  val parser: RowParser[Item] = {
    get[Option[Long]]("items.id_item") ~
      get[String]("items.name") ~
      get[Option[String]]("items.description") ~
      get[Option[LocalDateTime]]("items.registration_date") ~
      get[Option[Long]]("items.id_user") ~
      get[Int]("items.id_item_status") map {
      case id ~ name ~ description ~ registrationDate ~ idUser ~ idItemStatus =>
        Item(id, name, description, registrationDate, idUser, Option(ItemStatus(idItemStatus)))
    }
  }

  implicit object ItemFormat extends Format[Item] {
    override def reads(json: JsValue): JsResult[Item] =
      ((JsPath \ "id").readNullable[Long]
        and (JsPath \ "name").read[String]
        and (JsPath \ "description").readNullable[String]
        and (JsPath \ "registrationDate").readNullable[LocalDateTime]
        and (JsPath \ "idUser").readNullable[Long]
        and (JsPath \ "itemStatus").readNullable[ItemStatus]) (Item.apply _)
        .reads(json)

    override def writes(item: Item): JsValue = Json.obj(
      "id" -> item.id,
      "name" -> item.name,
      "description" -> item.description,
      "registrationDate" -> item.registrationDate,
      "idUser" -> item.idUser,
      "itemStatus" -> item.itemStatus
    )
  }

}
