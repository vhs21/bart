package controllers

import auth.{AuthenticatedAction, NonAuthenticatedAction}
import com.google.inject.Inject
import models.{Role, User}
import play.api.libs.json.Json
import play.api.mvc._
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(
                                val userRepository: UserRepository,
                                val authenticatedAction: AuthenticatedAction,
                                val nonAuthenticatedAction: NonAuthenticatedAction,
                                val cc: ControllerComponents)
                              (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def selectAll = authenticatedAction(Role.ADMIN).async { implicit request =>
    userRepository.selectAll map { users =>
      Ok(Json.toJson(users))
    }
  }

  def select(id: Long) = authenticatedAction.async { implicit request =>
    userRepository.select(id) map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound
    }
  }

  def insert = nonAuthenticatedAction(parse.json).async { implicit request =>
    request.body.validate[User].fold(
      invalid => Future.successful(BadRequest),
      user => userRepository.insert(user) map (_ => Ok)
    )
  }

  def delete(id: Long) = authenticatedAction(Role.ADMIN).async { implicit request =>
    userRepository.delete(id) map { deleted =>
      if (deleted > 0) Ok
      else NotFound
    }
  }

  def update(id: Long) = authenticatedAction(parse.json).async { implicit request =>
    request.body.validate[User].fold(
      invalid => Future.successful(BadRequest),
      user => userRepository.update(id, user) map { updated =>
        if (updated > 0) Ok
        else NotFound
      }
    )
  }

}
