package controllers

import auth.{AuthenticatedAction, AuthenticatedRoleAction, NonAuthenticatedAction}
import com.google.inject.Inject
import forms.SignUpForm
import models.Role.Role
import models.{Role, User}
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()
(userRepository: UserRepository, authenticatedAction: AuthenticatedAction,
 nonAuthenticatedAction: NonAuthenticatedAction, authenticatedRoleAction: AuthenticatedRoleAction,
 cc: ControllerComponents)
(implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  def index: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(views.html.index()))
  }

  def findAll: Action[AnyContent] = authenticatedAction.async { implicit request =>
    userRepository.findAll map { users =>
      Ok(views.html.userList(users))
    }
  }

  def findAllManagement: Action[AnyContent] = authenticatedRoleAction(Role.ADMIN).async { implicit request =>
    userRepository.findAll map { users =>
      Ok(views.html.admin.userList(users))
    }
  }

  def findById(id: Long): Action[AnyContent] = authenticatedAction.async { implicit request =>
    userRepository.findById(id).map { user =>
      Ok(views.html.profile(user))
    }
  }

  def add: Action[AnyContent] = nonAuthenticatedAction.async { implicit request =>
    SignUpForm.form.bindFromRequest.fold(
      { formWithErrors =>
        Future.successful(BadRequest(views.html.signUp(formWithErrors)))
      },
      { data =>
        val user = User(None, data.username, data.email, data.password)
        userRepository.add(user).map { _ =>
          Redirect(routes.UserController.index()).flashing("info" -> "User added!")
        }
      }
    )
  }

  def remove(id: Long): Action[AnyContent] = authenticatedAction.async { implicit request =>
    userRepository.remove(id) map { _ =>
      Redirect(routes.UserController.findAll()).flashing("info" -> "User deleted!")
    }
  }

  def openSignUp: Action[AnyContent] = nonAuthenticatedAction.async { implicit request =>
    Future.successful(Ok(views.html.signUp(SignUpForm.form)))
  }

  def changeRole(id: Long, roleId: Int): Action[AnyContent] = authenticatedRoleAction(Role.ADMIN).async { implicit request =>
    Role(roleId) match {
      case Role.USER => changeRole(id, Role.MANAGER)
      case Role.MANAGER => changeRole(id, Role.USER)
      case _ => Future.successful(Redirect(routes.UserController.findAllManagement()).flashing("info" -> "Can't change role!"))
    }
  }

  private def changeRole(userId: Long, newRole: Role) = {
    userRepository.changeRole(userId, newRole) map { _ =>
      Redirect(routes.UserController.findAllManagement()).flashing("info" -> "Role changed!")
    }
  }

}
