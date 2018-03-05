package models

import java.time.LocalDateTime

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import play.api.libs.json._

case class Item(
                 id: Option[Long],
                 name: String,
                 description: Option[String],
                 idUser: Long,
                 registrationDate: Option[LocalDateTime])

object Item {

  val parser: RowParser[Item] = {
    get[Option[Long]]("items.id_item") ~
      get[String]("items.name") ~
      get[Option[String]]("items.description") ~
      get[Long]("items.id_user") ~
      get[Option[LocalDateTime]]("items.registration_date") map {
      case id ~ name ~ description ~ user ~ registrationDate =>
        Item(id, name, description, user, registrationDate)
    }
  }

  implicit object ItemFormat extends Format[Item] {
    override def reads(json: JsValue): JsResult[Item] = JsSuccess(
      Item(
        id = (json \ "id").asOpt[Long],
        name = (json \ "name").as[String],
        description = (json \ "description").asOpt[String],
        idUser = (json \ "idUser").as[Long],
        registrationDate = (json \ "registrationDate").asOpt[LocalDateTime])
    )

    override def writes(item: Item): JsValue = Json.obj(
      "id" -> item.id,
      "name" -> item.name,
      "description" -> item.description,
      "idUser" -> item.idUser,
      "registrationDate" -> item.registrationDate
    )
  }

}
