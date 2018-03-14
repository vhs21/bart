package controllers

import auth.{AuthenticatedAction, NonAuthenticatedAction}
import com.google.inject.Inject
import models.{Role, User}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import repositories.UserRepository

import scala.concurrent.ExecutionContext

class UserController @Inject()(
                                val userRepository: UserRepository,
                                val authenticatedAction: AuthenticatedAction,
                                val nonAuthenticatedAction: NonAuthenticatedAction,
                                override val cc: ControllerComponents)
                              (implicit override val ec: ExecutionContext)
  extends ModelController(userRepository, cc) {

  def selectAll: Action[AnyContent] = authenticatedAction(Role.ADMIN).async { implicit request =>
    selectAll((users: Seq[User]) => Json.toJson(users))
  }

  def select(id: Long): Action[AnyContent] = authenticatedAction.async { implicit request =>
    select(id, Json.toJson[User])
  }

  def insert: Action[JsValue] = nonAuthenticatedAction.async(parse.json) { implicit request =>
    insert(request.body.validate[User])
  }

  def update(id: Long): Action[JsValue] = authenticatedAction.async(parse.json) { implicit request =>
    update(id, request.body.validate[User])
  }

}
