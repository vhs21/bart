package controllers

import auth.AuthenticatedAction
import com.google.inject.Inject
import models.{Item, Role}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import repositories.ItemRepository

import scala.concurrent.ExecutionContext

class ItemController @Inject()(
                                val itemRepository: ItemRepository,
                                val authenticatedAction: AuthenticatedAction,
                                override val cc: ControllerComponents)
                              (implicit override val ec: ExecutionContext)
  extends ModelController[Item](itemRepository, cc) {

  def selectAll: Action[AnyContent] = Action.async { implicit request =>
    selectAll((items: Seq[Item]) => Json.toJson(items))
  }

  def select(id: Long): Action[AnyContent] = Action.async { implicit request =>
    select(id, Json.toJson[Item])
  }

  def insert: Action[JsValue] = authenticatedAction(parse.json).async { implicit request =>
    insert(request.body.validate[Item])
  }

  def update(id: Long): Action[JsValue] = authenticatedAction.async(parse.json) { implicit request =>
    update(id, request.body.validate[Item])
  }

  def updateStatus(id: Long, idStatus: Int): Action[AnyContent] = authenticatedAction(Role.MANAGER, Role.ADMIN)
    .async { implicit request =>
      itemRepository.updateStatus(id, idStatus) map { updated =>
        if (updated > 0) Ok
        else NotFound
      }
    }

}
