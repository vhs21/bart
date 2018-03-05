package controllers

import auth.AuthenticatedAction
import com.google.inject.Inject
import models.{Item, Role}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import repositories.ItemRepository

import scala.concurrent.{ExecutionContext, Future}

class ItemController @Inject()(
                                val itemRepository: ItemRepository,
                                val authenticatedAction: AuthenticatedAction,
                                val cc: ControllerComponents)
                              (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def selectAll = Action.async { implicit request =>
    itemRepository.selectAll map { items =>
      Ok(Json.toJson(items))
    }
  }

  def select(id: Long) = Action.async { implicit request =>
    itemRepository.select(id) map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound
    }
  }

  def insert = authenticatedAction(parse.json).async { implicit request =>
    request.body.validate[Item].fold(
      invalid => Future.successful(BadRequest),
      item => itemRepository.insert(item) map (_ => Ok)
    )
  }

  def delete(id: Long) = authenticatedAction.async { implicit request =>
    itemRepository.delete(id) map { deleted =>
      if (deleted > 0) Ok
      else NotFound
    }
  }

  def update(id: Long) = authenticatedAction(parse.json).async { implicit request =>
    request.body.validate[Item].fold(
      invalid => Future.successful(BadRequest),
      item => itemRepository.update(id, item) map { updated =>
        if (updated > 0) Ok
        else NotFound
      }
    )
  }

  def updateStatus(id: Long, idStatus: Int) = authenticatedAction(Role.MANAGER, Role.ADMIN)
    .async { implicit request =>
      itemRepository.updateStatus(id, idStatus) map { updated =>
        if (updated > 0) Ok
        else NotFound
      }
    }

}
