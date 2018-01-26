package repositories.impl

import models.User
import repositories.UserRepository
import anorm.SqlParser._
import anorm._
import com.google.inject.{Inject, Singleton}
import forms.LogInForm
import play.api.db.DBApi

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class UserRepositoryImpl @Inject()(dbapi: DBApi)(implicit ec: ExecutionContext) extends UserRepository {

  private val db = dbapi.database("default")

  private val user = {
    get[Option[Long]]("users.id_user") ~
      get[String]("users.username") ~
      get[String]("users.email") ~
      get[String]("users.password") map {
      case id ~ username ~ email ~ password =>
        User(id, username, email, password)
    }
  }

  override def findAll: Future[Seq[User]] = Future {
    db.withConnection { implicit connection =>
      SQL"select id_user, username, email, password from users".as(user *)
    }
  }(ec)

  override def findById(id: Long): Future[Option[User]] = Future {
    db.withConnection { implicit connection =>
      SQL"select id_user, username, email, password from users where id_user = $id"
        .as(user.singleOpt)
    }
  }(ec)

  override def add(user: User): Future[Int] = Future {
    db.withConnection { implicit connection =>
      SQL"insert into users(username, email, password) values(${user.username}, ${user.email}, ${user.password})".executeUpdate()
    }
  }(ec)

  override def remove(id: Long): Future[Int] = Future {
    db.withConnection { implicit connection =>
      SQL"delete from users where id_user = $id".executeUpdate()
    }
  }(ec)

  override def exists(logInData: LogInForm.Data): Future[Boolean] = Future {
    db.withConnection { implicit connection =>
      SQL"select id_user, username, email, password from users where username = ${logInData.username} and password = ${logInData.password}"
        .as(this.user.singleOpt).isDefined
    }
  }(ec)

}
