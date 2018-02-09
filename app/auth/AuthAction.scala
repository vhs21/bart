package auth

import com.google.inject.{Inject, Singleton}
import models.Role.Role
import models.User
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedRequest[A](val user: User, val request: Request[A])
  extends WrappedRequest[A](request)

@Singleton
class AuthenticatedAction @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[AuthenticatedRequest, AnyContent] with ActionRefiner[Request, AuthenticatedRequest] {

  override protected def refine[A](request: Request[A]): Future[Either[Result, AuthenticatedRequest[A]]] = Future {
    request.session.get(Authenticator.USER)
      .map(userJson => new AuthenticatedRequest(Authenticator.deserializeUser(userJson), request))
      .toRight(Results.Forbidden)
  }

}

@Singleton
class NonAuthenticatedAction @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[Request, AnyContent] with ActionFilter[Request] {

  override protected def filter[A](request: Request[A]): Future[Option[Result]] = Future {
    request.session.get(Authenticator.USER) match {
      case Some(_) => Some(Results.Forbidden)
      case _ => None
    }
  }

}

@Singleton
class AuthenticatedRoleAction @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[AuthenticatedRequest, AnyContent] with ActionRefiner[Request, AuthenticatedRequest] {

  override protected def refine[A](request: Request[A]): Future[Either[Result, AuthenticatedRequest[A]]] = Future.successful(
    request.session.get(Authenticator.USER)
      .map(userJson => new AuthenticatedRequest(Authenticator.deserializeUser(userJson), request))
      .toRight(Results.Forbidden)
  )

  private def checkRole(role: Role)(implicit ec: ExecutionContext): ActionFilter[AuthenticatedRequest]
  = new ActionFilter[AuthenticatedRequest] {

    override protected def executionContext: ExecutionContext = ec

    override protected def filter[A](request: AuthenticatedRequest[A]): Future[Option[Result]] = Future.successful {
      if (request.user.role == role) None else Some(Results.Forbidden)
    }

  }

  def apply(role: Role): ActionBuilder[AuthenticatedRequest, AnyContent] = this andThen checkRole(role)

}
