package models

import java.time.LocalDateTime

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import models.ItemStatus.ItemStatus
import play.api.libs.json._

case class Item(
                 id: Option[Long],
                 name: String,
                 description: Option[String],
                 idUser: Long,
                 registrationDate: Option[LocalDateTime],
                 itemStatus: ItemStatus)

object Item {

  val parser: RowParser[Item] = {
    get[Option[Long]]("items.id_item") ~
      get[String]("items.name") ~
      get[Option[String]]("items.description") ~
      get[Long]("items.id_user") ~
      get[Option[LocalDateTime]]("items.registration_date") ~
      get[Int]("items.id_item_status") map {
      case id ~ name ~ description ~ user ~ registrationDate ~ idItemStatus =>
        Item(id, name, description, user, registrationDate, ItemStatus(idItemStatus))
    }
  }

  implicit object ItemFormat extends Format[Item] {
    override def reads(json: JsValue): JsResult[Item] = JsSuccess(
      Item(
        id = (json \ "id").asOpt[Long],
        name = (json \ "name").as[String],
        description = (json \ "description").asOpt[String],
        idUser = (json \ "idUser").as[Long],
        registrationDate = (json \ "registrationDate").asOpt[LocalDateTime],
        itemStatus = (json \ "itemStatus").as[ItemStatus])
    )

    override def writes(item: Item): JsValue = Json.obj(
      "id" -> item.id,
      "name" -> item.name,
      "description" -> item.description,
      "idUser" -> item.idUser,
      "registrationDate" -> item.registrationDate,
      "itemStatus" -> item.itemStatus
    )
  }

}
