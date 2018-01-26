package controllers

import com.google.inject.Inject
import forms.SignUpForm
import models.User
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents, _}
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()
(userRepository: UserRepository,
 cc: MessagesControllerComponents)
(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def index = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.index())
  }

  def findAll: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    userRepository.findAll map { users =>
      Ok(views.html.userList(users))
    }
  }

  def findById(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    userRepository.findById(id).map { user =>
      Ok(views.html.profile(user))
    }
  }

  def add: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
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

  def remove(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    userRepository.remove(id) map { _ =>
      Redirect(routes.UserController.findAll()).flashing("info" -> "User deleted!")
    }
  }

  def openSignUp: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.signUp(SignUpForm.form))
  }

}
