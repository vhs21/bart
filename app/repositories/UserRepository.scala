package repositories

import com.google.inject.ImplementedBy
import forms.LogInForm
import models.Role.Role
import models.User
import repositories.impl.UserRepositoryImpl

import scala.concurrent.Future

@ImplementedBy(classOf[UserRepositoryImpl])
trait UserRepository {

  def findAll: Future[Seq[User]]

  def findById(id: Long): Future[Option[User]]

  def add(user: User): Future[Int]

  def remove(id: Long): Future[Int]

  def findWithCredentials(logInData: LogInForm.Data): Future[Option[User]]

  def changeRole(userId: Long, newRole: Role): Future[Boolean]

}