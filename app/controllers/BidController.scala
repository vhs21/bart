package controllers

import auth.AuthenticatedAction
import com.google.inject.Inject
import models.{Bid, Role}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import repositories.BidRepository

import scala.concurrent.{ExecutionContext, Future}

class BidController @Inject()(
                               val bidRepository: BidRepository,
                               val authenticatedAction: AuthenticatedAction,
                               val cc: ControllerComponents)
                             (implicit val ec: ExecutionContext)
  extends AbstractController(cc) {

  def selectAll: Action[AnyContent] = authenticatedAction.async { implicit request =>
    bidRepository.selectAll map { bids =>
      Ok(Json.toJson(bids))
    }
  }

  def select(id: Long): Action[AnyContent] = authenticatedAction.async { implicit request =>
    bidRepository.select(id) map {
      case Some(bid) => Ok(Json.toJson(bid))
      case None => NotFound
    }
  }

  def insert: Action[JsValue] = authenticatedAction.async(parse.json) { implicit request =>
    request.body.validate[Bid].fold(
      invalid => Future.successful(BadRequest),
      bid => bidRepository.insert(bid) map (_ => Ok)
    )
  }

  def delete(id: Long): Action[AnyContent] = authenticatedAction(Role.ADMIN).async { implicit request =>
    bidRepository.delete(id) map {
      case Some(deleted) => if (deleted > 0) Ok else NotFound
      case None => BadRequest
    }
  }

  def update(id: Long): Action[JsValue] = authenticatedAction.async(parse.json) { implicit request =>
    request.body.validate[Bid].fold(
      invalid => Future.successful(BadRequest),
      bid => bidRepository.update(id, bid) map {
        case Some(updated) => if (updated > 0) Ok else NotFound
        case None => BadRequest
      }
    )
  }

}
