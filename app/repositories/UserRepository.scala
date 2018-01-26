package repositories

import com.google.inject.ImplementedBy
import forms.LogInForm
import models.User
import repositories.impl.UserRepositoryImpl

import scala.concurrent.Future

@ImplementedBy(classOf[UserRepositoryImpl])
trait UserRepository extends {

  def findAll: Future[Seq[User]]

  def findById(id: Long): Future[Option[User]]

  def add(user: User): Future[Int]

  def remove(id: Long): Future[Int]

  def exists(logInData: LogInForm.Data): Future[Boolean]

}