package models

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Bid(
                id: Option[Long],
                idItemGoal: Long,
                idItemOffer: Long,
                isAccepted: Boolean = false)

object Bid {

  val parser: RowParser[Bid] = {
    get[Option[Long]]("bids.id_bid") ~
      get[Long]("bids.id_item_goal") ~
      get[Long]("bids.id_item_offer") ~
      get[Boolean]("bids.is_accepted") map {
      case id ~ idGoalItem ~ idOfferItem ~ isAccepted =>
        Bid(id, idGoalItem, idOfferItem, isAccepted)
    }
  }

  implicit object BidFormat extends Format[Bid] {
    override def reads(json: JsValue): JsResult[Bid] =
      ((JsPath \ "id").readNullable[Long]
        and (JsPath \ "idItemGoal").read[Long]
        and (JsPath \ "idItemOffer").read[Long]
        and (JsPath \ "isAccepted").readNullable[Boolean].map(_.getOrElse(false))) (Bid.apply _)
        .reads(json)

    override def writes(bid: Bid): JsValue = Json.obj(
      "id" -> bid.id,
      "idItemGoal" -> bid.idItemGoal,
      "idItemOffer" -> bid.idItemOffer,
      "isAccepted" -> bid.isAccepted
    )
  }

}
