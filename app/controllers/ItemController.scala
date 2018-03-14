package controllers

import auth.AuthenticatedAction
import com.google.inject.Inject
import models.{Item, Role}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import repositories.ItemRepository

import scala.concurrent.{ExecutionContext, Future}

class ItemController @Inject()(
                                val itemRepository: ItemRepository,
                                val authenticatedAction: AuthenticatedAction,
                                val cc: ControllerComponents)
                              (implicit val ec: ExecutionContext)
  extends AbstractController(cc) {

  def selectAll: Action[AnyContent] = Action.async { implicit request =>
    itemRepository.selectAll map { items =>
      Ok(Json.toJson(items))
    }
  }

  def select(id: Long): Action[AnyContent] = Action.async { implicit request =>
    itemRepository.select(id) map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound
    }
  }

  def insert: Action[JsValue] = authenticatedAction(parse.json).async { implicit request =>
    request.body.validate[Item].fold(
      invalid => Future.successful(BadRequest),
      item => itemRepository.insert(item) map (_ => Ok)
    )
  }

  def delete(id: Long): Action[AnyContent] = authenticatedAction(Role.USER).async { implicit request =>
    itemRepository.delete(id) map {
      case Some(deleted) => if (deleted > 0) Ok else NotFound
      case None => BadRequest
    }
  }

  def update(id: Long): Action[JsValue] = authenticatedAction.async(parse.json) { implicit request =>
    request.body.validate[Item].fold(
      invalid => Future.successful(BadRequest),
      item => itemRepository.update(id, item) map {
        case Some(updated) => if (updated > 0) Ok else NotFound
        case None => BadRequest
      }
    )
  }

  def updateStatus(id: Long, idStatus: Int): Action[AnyContent] = authenticatedAction(Role.MANAGER, Role.ADMIN)
    .async { implicit request =>
      itemRepository.updateStatus(id, idStatus) map { updated =>
        if (updated > 0) Ok
        else NotFound
      }
    }

}
