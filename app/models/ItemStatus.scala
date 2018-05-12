package models

object ItemStatus extends Enumeration {

  import play.api.libs.json._

  type ItemStatus = Value

  val CREATED: Value = Value(1)
  val APPROVED: Value = Value(2)
  val REJECTED: Value = Value(3)
  val IN_BID: Value = Value(4)

  implicit object ItemStatusFormat extends Format[ItemStatus] {
    override def reads(json: JsValue): JsResult[ItemStatus] =
      (JsPath \ "id").read[Int].map(ItemStatus.apply).reads(json)

    override def writes(itemStatus: ItemStatus): JsValue = Json.obj(
      "id" -> itemStatus.id,
      "name" -> itemStatus.toString
    )
  }

}
