package models

object ItemStatus extends Enumeration {

  import play.api.libs.json._

  type ItemStatus = Value

  val CREATED: Value = Value(1)
  val APPROVED: Value = Value(2)
  val REJECTED: Value = Value(3)

  implicit object ItemStatusFormat extends Format[ItemStatus] {
    override def writes(itemStatus: ItemStatus): JsValue = Json.obj(
      "id" -> itemStatus.id,
      "name" -> itemStatus.toString
    )

    override def reads(json: JsValue): JsResult[ItemStatus] =
      JsSuccess(ItemStatus((json \ "id").as[Int]))
  }

}
