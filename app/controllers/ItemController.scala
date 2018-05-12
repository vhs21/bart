package controllers

import java.time.LocalDateTime

import auth.AuthenticatedAction
import com.google.inject.Inject
import models.{Item, ItemStatus, Role}
import play.api.libs.Files
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import repositories.{ItemRepository, PhotoRepository}
import utils.{ItemSearchCriteria, PhotoManager}

import scala.concurrent.{ExecutionContext, Future}

class ItemController @Inject()(
                                val itemRepository: ItemRepository,
                                val photoRepository: PhotoRepository,
                                val authenticatedAction: AuthenticatedAction,
                                val photoManager: PhotoManager,
                                override val cc: ControllerComponents)
                              (implicit override val ec: ExecutionContext)
  extends ModelController[Item](itemRepository, cc) {

  def selectAll(limit: Int, offset: Int, id: Option[Long], description: Option[String],
                registrationDate: Option[String], idUser: Option[Long], idItemStatus: Option[Int])
  : Action[AnyContent] = Action.async { implicit request =>
    itemRepository.selectAll(
      new ItemSearchCriteria(
        limit = limit, offset = offset, id = id, description = description,
        registrationDate = registrationDate.map(LocalDateTime.parse(_)),
        idUser = idUser, itemStatus = idItemStatus.map(ItemStatus(_))
      )
    ).map { items => Ok(Json.toJson(items)) }
  }

  def select(id: Long): Action[AnyContent] = Action.async { implicit request =>
    select(id, Json.toJson[Item])
  }

  def insert: Action[MultipartFormData[Files.TemporaryFile]] =
    authenticatedAction.async(parse.multipartFormData) { implicit request =>
      request.body.dataParts.get("item") match {
        case Some(itemParts) => Json.parse(itemParts.mkString).validate[Item].fold(
          invalid => Future.successful(BadRequest),
          item => itemRepository.insert(Item(
            name = item.name,
            description = item.description,
            idUser = request.user.id)) map {
            case Some(idItem) =>
              request.body.files.foreach(file =>
                photoRepository.insert(photoManager.save(file, idItem)))
              Ok
            case None => InternalServerError
          }
        )
        case None => Future.successful(BadRequest)
      }
    }

  def update(id: Long): Action[JsValue] = authenticatedAction.async(parse.json) { implicit request =>
    update(id, request.body.validate[Item])
  }

  def updateStatus(id: Long, idStatus: Int): Action[AnyContent] = authenticatedAction(Role.ADMIN)
    .async { implicit request =>
      itemRepository.updateStatus(id, idStatus) map { updated =>
        if (updated > 0) Ok
        else NotFound
      }
    }

  def count(id: Option[Long], description: Option[String], registrationDate: Option[String],
            idUser: Option[Long], idItemStatus: Option[Int])
  : Action[AnyContent] = Action.async { implicit request =>
    itemRepository.count(
      new ItemSearchCriteria(
        id = id, description = description,
        registrationDate = registrationDate.map(LocalDateTime.parse(_)),
        idUser = idUser, itemStatus = idItemStatus.map(ItemStatus(_))
      )
    ).map { count => Ok(Json.toJson(count)) }
  }

}
