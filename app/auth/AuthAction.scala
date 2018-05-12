package auth

import com.google.inject.{Inject, Singleton}
import models.Role.Role
import models.{Role, User}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedRequest[A](val user: User, val request: Request[A])
  extends WrappedRequest[A](request)

@Singleton
class AuthenticatedAction @Inject()
(val parser: BodyParsers.Default, val jwtAuthenticator: JwtAuthenticator)
(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[AuthenticatedRequest, AnyContent] with ActionRefiner[Request, AuthenticatedRequest] {
  override protected def refine[A](request: Request[A]): Future[Either[Result, AuthenticatedRequest[A]]] = Future {
    request.headers.get(jwtAuthenticator.HEADER) match {
      case Some(token) if jwtAuthenticator.isValidToken(token) =>
        jwtAuthenticator.decodePayload(token) match {
          case Some(user) => Right(new AuthenticatedRequest(user, request))
          case None => Left(Results.Forbidden)
        }
      case None => Left(Results.Unauthorized)
    }
  }

  private def checkRole(roles: Seq[Role])(implicit ec: ExecutionContext): ActionFilter[AuthenticatedRequest]
  = new ActionFilter[AuthenticatedRequest] {
    override protected def executionContext: ExecutionContext = ec

    override protected def filter[A](request: AuthenticatedRequest[A]): Future[Option[Result]] = Future.successful {
      if (roles.contains(request.user.role.getOrElse(Role.USER))) None
      else Some(Results.Forbidden)
    }
  }

  def apply(roles: Role*): ActionBuilder[AuthenticatedRequest, AnyContent] = this andThen checkRole(roles)
}

@Singleton
class NonAuthenticatedAction @Inject()
(val parser: BodyParsers.Default, val jwtAuthenticator: JwtAuthenticator)
(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[Request, AnyContent] with ActionFilter[Request] {
  override protected def filter[A](request: Request[A]): Future[Option[Result]] = Future {
    request.headers.get(jwtAuthenticator.HEADER) match {
      case Some(_) => Some(Results.Forbidden)
      case None => None
    }
  }
}
