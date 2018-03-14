package controllers

import auth.AuthenticatedAction
import com.google.inject.Inject
import models.Bid
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import repositories.BidRepository

import scala.concurrent.ExecutionContext

class BidController @Inject()(
                               val bidRepository: BidRepository,
                               val authenticatedAction: AuthenticatedAction,
                               override val cc: ControllerComponents)
                             (implicit override val ec: ExecutionContext)
  extends ModelController(bidRepository, cc) {

  def selectAll: Action[AnyContent] = authenticatedAction.async { implicit request =>
    selectAll((bids: Seq[Bid]) => Json.toJson(bids))
  }

  def select(id: Long): Action[AnyContent] = authenticatedAction.async { implicit request =>
    select(id, Json.toJson[Bid])
  }

  def insert: Action[JsValue] = authenticatedAction.async(parse.json) { implicit request =>
    insert(request.body.validate[Bid])
  }

}
