package controllers

import auth.{AuthenticatedAction, NonAuthenticatedAction}
import com.google.inject.Inject
import forms.SignUpForm
import models.User
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()
(userRepository: UserRepository,
 authenticatedAction: AuthenticatedAction,
 nonAuthenticatedAction: NonAuthenticatedAction,
 cc: ControllerComponents)
(implicit ec: ExecutionContext)
  extends AbstractController(cc) with I18nSupport {

  def index: Action[AnyContent] = Action.async { implicit request =>
    Future {
      Ok(views.html.index())
    }
  }

  def findAll: Action[AnyContent] = authenticatedAction.async { implicit request =>
    userRepository.findAll map { users =>
      Ok(views.html.userList(users))
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
    Future {
      Ok(views.html.signUp(SignUpForm.form))
    }
  }

}
