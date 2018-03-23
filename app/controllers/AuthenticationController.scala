package controllers

import auth.{JwtAuthenticator, NonAuthenticatedAction}
import com.google.inject.Inject
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Json, Reads}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

class AuthenticationController @Inject()(
                                         val userRepository: UserRepository,
                                         val jwtAuthenticator: JwtAuthenticator,
                                         val nonAuthenticatedAction: NonAuthenticatedAction,
                                         val cc: ControllerComponents)
                                       (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  case class AuthForm(username: String, password: String) {}

  implicit val authFormReads: Reads[AuthForm] =
    ((JsPath \ "username").read[String] and
      (JsPath \ "password").read[String]) (AuthForm.apply _)

  def authenticate: Action[JsValue] = nonAuthenticatedAction(parse.json).async { implicit request =>
    request.body.validate[AuthForm].fold(
      invalid => Future.successful(BadRequest),
      authForm => userRepository.authenticate(authForm.username, authForm.password) map {
        case Some(user) => Ok(Json.toJson(jwtAuthenticator.createToken(user)))
        case None => Forbidden
      }
    )
  }

}
