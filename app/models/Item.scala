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
                 registrationDate: Option[LocalDateTime],
                 idUser: Long,
                 itemStatus: Option[ItemStatus])

object Item {

  val parser: RowParser[Item] = {
    get[Option[Long]]("items.id_item") ~
      get[String]("items.name") ~
      get[Option[String]]("items.description") ~
      get[Option[LocalDateTime]]("items.registration_date") ~
      get[Long]("items.id_user") ~
      get[Int]("items.id_item_status") map {
      case id ~ name ~ description ~ registrationDate ~ user ~ idItemStatus =>
        Item(id, name, description, registrationDate, user, Option(ItemStatus(idItemStatus)))
    }
  }

  implicit object ItemFormat extends Format[Item] {
    override def reads(json: JsValue): JsResult[Item] = JsSuccess(
      Item(
        id = (json \ "id").asOpt[Long],
        name = (json \ "name").as[String],
        description = (json \ "description").asOpt[String],
        registrationDate = (json \ "registrationDate").asOpt[LocalDateTime],
        idUser = (json \ "idUser").as[Long],
        itemStatus = (json \ "itemStatus").asOpt[ItemStatus])
    )

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
