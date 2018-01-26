package controllers

import com.google.inject.Inject
import forms.LogInForm
import play.api.mvc._
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

class LogInController @Inject()
(cc: MessagesControllerComponents,
 userRepository: UserRepository)
(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  def openLogIn: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(views.html.logIn(LogInForm.form)))
  }

  def logIn: Action[AnyContent] = Action.async { implicit request =>
    LogInForm.form.bindFromRequest.fold(
      { formWithErrors =>
        Future.successful(BadRequest(views.html.logIn(formWithErrors)))
      },
      { data =>
        userRepository.exists(data) map { exists =>
          if (exists) {
            Redirect(routes.UserController.index()).flashing("info" -> "Successful log in!")
          }
          else {
            Redirect(routes.LogInController.openLogIn()).flashing("info" -> "User not found!")
          }
        }
      }
    )
  }

}
