package controllers

import auth.{AuthenticatedAction, NonAuthenticatedAction}
import com.google.inject.Inject
import models.{Role, User}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(
                                val userRepository: UserRepository,
                                val authenticatedAction: AuthenticatedAction,
                                val nonAuthenticatedAction: NonAuthenticatedAction,
                                val cc: ControllerComponents)
                              (implicit val ec: ExecutionContext)
  extends AbstractController(cc) {

  def selectAll: Action[AnyContent] = authenticatedAction(Role.ADMIN).async { implicit request =>
    userRepository.selectAll map { users =>
      Ok(Json.toJson(users))
    }
  }

  def select(id: Long): Action[AnyContent] = authenticatedAction.async { implicit request =>
    userRepository.select(id) map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound
    }
  }

  def insert: Action[JsValue] = nonAuthenticatedAction.async(parse.json) { implicit request =>
    request.body.validate[User].fold(
      invalid => Future.successful(BadRequest),
      user => userRepository.insert(user) map (_ => Ok)
    )
  }

  def delete(id: Long): Action[AnyContent] = authenticatedAction(Role.ADMIN).async { implicit request =>
    userRepository.delete(id) map {
      case Some(deleted) => if (deleted > 0) Ok else NotFound
      case None => BadRequest
    }
  }

  def update(id: Long): Action[JsValue] = authenticatedAction.async(parse.json) { implicit request =>
    request.body.validate[User].fold(
      invalid => Future.successful(BadRequest),
      user => userRepository.update(id, user) map {
        case Some(updated) => if (updated > 0) Ok else NotFound
        case None => BadRequest
      }
    )
  }

}
