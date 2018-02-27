package repositories

import com.google.inject.ImplementedBy
import models.User
import repositories.impl.UserRepositoryImpl

import scala.concurrent.Future

@ImplementedBy(classOf[UserRepositoryImpl])
trait UserRepository {

  def selectAll: Future[Seq[User]]

  def select(id: Long): Future[Option[User]]

  def insert(user: User): Future[Option[Long]]

  def delete(id: Long): Future[Int]

  def update(id: Long, user: User): Future[Int]

  def authenticate(username: String, password:String): Future[Option[User]]

}