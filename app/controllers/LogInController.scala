package controllers

import auth.{AuthenticatedAction, Authenticator, NonAuthenticatedAction}
import com.google.inject.Inject
import forms.LogInForm
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

class LogInController @Inject()
(cc: ControllerComponents,
 authenticatedAction: AuthenticatedAction,
 nonAuthenticatedAction: NonAuthenticatedAction,
 userRepository: UserRepository)
(implicit ec: ExecutionContext)
  extends AbstractController(cc) with I18nSupport {

  def openLogIn: Action[AnyContent] = nonAuthenticatedAction.async { implicit request =>
    Future.successful(Ok(views.html.logIn(LogInForm.form)))
  }

  def logIn: Action[AnyContent] = nonAuthenticatedAction.async { implicit request =>
    LogInForm.form.bindFromRequest.fold(
      { formWithErrors =>
        Future.successful(BadRequest(views.html.logIn(formWithErrors)))
      },
      { data =>
        userRepository.findWithCredentials(data) map {
          case Some(user) => Redirect(routes.UserController.index())
            .withSession(Authenticator.USER -> Authenticator.serializeUser(user))
            .flashing("info" -> "Successful log in!")
          case None => Redirect(routes.LogInController.openLogIn())
            .flashing("info" -> "User not found!")
        }
      }
    )
  }

  def logOut: Action[AnyContent] = authenticatedAction.async { implicit request =>
    Future {
      Redirect(routes.UserController.index())
        .withNewSession
        .flashing("info" -> "Log out done.")
    }
  }

}
