package models

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Photo(
                  id: String,
                  idItem: Long)

object Photo {

  val parser: RowParser[Photo] = {
    get[String]("photos.id_photo") ~
      get[Long]("photos.id_item") map {
      case id ~ idItem =>
        Photo(id, idItem)
    }
  }

  implicit object PhotoFormat extends Format[Photo] {
    override def reads(json: JsValue): JsResult[Photo] =
      ((JsPath \ "id").read[String]
        and (JsPath \ "idItem").read[Long]) (Photo.apply _)
        .reads(json)

    override def writes(photo: Photo): JsValue = Json.obj(
      "id" -> photo.id,
      "idItem" -> photo.idItem
    )
  }

}
