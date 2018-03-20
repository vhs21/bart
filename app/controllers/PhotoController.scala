package controllers

import auth.{AuthenticatedAction, NonAuthenticatedAction}
import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import repositories.PhotoRepository
import utils.PhotoManager

import scala.concurrent.ExecutionContext

class PhotoController @Inject()(
                                 val photoRepository: PhotoRepository,
                                 val authenticatedAction: AuthenticatedAction,
                                 val nonAuthenticatedAction: NonAuthenticatedAction,
                                 val photoManager: PhotoManager,
                                 override val cc: ControllerComponents)
                               (implicit override val ec: ExecutionContext)
  extends ModelController(photoRepository, cc) {

  def selectAll(idItem: Long): Action[AnyContent] = Action.async { implicit request =>
    photoRepository.selectAllWhereItem(idItem) map { photos =>
      Ok(Json.toJson(photos.map(photo => photoManager.load(photo.id))))
    }
  }

  def selectOne(idItem: Long, id: String): Action[AnyContent] = Action.async { implicit request =>
    photoRepository.selectOneWhereItem(idItem) map {
      case Some(photo) => Ok(Json.toJson(photoManager.load(photo.id)))
      case None => NoContent
    }
  }

}
