package repositories.impl

import models.{Role, User}
import repositories.UserRepository
import anorm.SqlParser._
import anorm._
import com.google.inject.{Inject, Singleton}
import forms.LogInForm
import org.mindrot.jbcrypt.BCrypt
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
      get[String]("users.password") ~
      get[Int]("users.id_role") map {
      case id ~ username ~ email ~ password ~ role =>
        User(id, username, email, password, Role(role))
    }
  }

  override def findAll: Future[Seq[User]] = Future {
    db.withConnection { implicit connection =>
      SQL"select id_user, username, email, password, id_role from users".as(user *)
    }
  }(ec)

  override def findById(id: Long): Future[Option[User]] = Future {
    db.withConnection { implicit connection =>
      SQL"select id_user, username, email, password, id_role from users where id_user = $id"
        .as(user.singleOpt)
    }
  }(ec)

  override def add(user: User): Future[Int] = Future {
    db.withConnection { implicit connection =>
      println(user)
      SQL"""insert into users(username, email, password, id_role) values(
        ${user.username},
        ${user.email},
        ${BCrypt.hashpw(user.password, BCrypt.gensalt())},
        ${user.role.id})"""
        .executeUpdate()
    }
  }(ec)

  override def remove(id: Long): Future[Int] = Future {
    db.withConnection { implicit connection =>
      SQL"delete from users where id_user = $id".executeUpdate()
    }
  }(ec)

  override def findWithCredentials(logInData: LogInForm.Data): Future[Option[User]] = Future {
    db.withConnection { implicit connection =>
      SQL"""select id_user, username, email, password, id_role from users
           where username = ${logInData.username}"""
        .as(user.singleOpt).filter(curUser => BCrypt.checkpw(logInData.password, curUser.password))
    }
  }(ec)

}
