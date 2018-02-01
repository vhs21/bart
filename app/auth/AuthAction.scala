package auth

import com.google.inject.Inject
import models.User
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedRequest[A](val user: User, val request: Request[A])
  extends WrappedRequest[A](request)

class AuthenticatedAction @Inject()
(parser: BodyParsers.Default)
(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    request.session.get(Authenticator.USER) match {
      case Some(user) =>
        block(new AuthenticatedRequest(Authenticator.deserializeUser(user), request))
      case None =>
        Future.successful(Results.Forbidden)
    }
  }

}

class NonAuthenticatedAction @Inject()
(parser: BodyParsers.Default)
(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    request.session.get(Authenticator.USER) match {
      case Some(_) =>
        Future.successful(Results.Forbidden)
      case None =>
        block(request)
    }
  }

}
