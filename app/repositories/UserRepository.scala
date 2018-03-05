package repositories

import com.google.inject.ImplementedBy
import models.User
import repositories.impl.UserRepositoryImpl

import scala.concurrent.Future

@ImplementedBy(classOf[UserRepositoryImpl])
trait UserRepository extends Repository[User] {

  def authenticate(username: String, password:String): Future[Option[User]]

}
