package repositories.impl

import anorm.SqlParser._
import anorm._
import com.google.inject.{Inject, Singleton}
import models.{Role, User}
import org.mindrot.jbcrypt.BCrypt
import play.api.db.DBApi
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class UserRepositoryImpl @Inject()(dbapi: DBApi)(implicit ec: ExecutionContext) extends UserRepository {

  private val db = dbapi.database("default")

  private val userMapper = {
    get[Option[Long]]("users.id_user") ~
      get[String]("users.username") ~
      get[String]("users.email") ~
      get[Option[String]]("users.password") ~
      get[Int]("users.id_role") map {
      case id ~ username ~ email ~ password ~ roleId =>
        User(id, username, email, password, Option(Role(roleId)))
    }
  }

  override def selectAll: Future[Seq[User]] = Future {
    db.withConnection { implicit connection =>
      SQL"""SELECT id_user, username, email, password, id_role
            FROM users""".as(userMapper *)
    }
  }(ec)

  override def select(id: Long): Future[Option[User]] = Future {
    db.withConnection { implicit connection =>
      SQL"""SELECT id_user, username, email, password, id_role
            FROM users
            WHERE id_user = $id""".as(userMapper.singleOpt)
    }
  }(ec)

  override def insert(user: User): Future[Option[Long]] = Future {
    db.withConnection { implicit connection =>
      SQL"""INSERT INTO users(username, email, password, id_role) VALUES(
            ${user.username},
            ${user.email},
            ${BCrypt.hashpw(user.password.get, BCrypt.gensalt())},
            ${user.role.getOrElse(Role.USER).id})""".executeInsert()
    }
  }(ec)

  override def delete(id: Long): Future[Int] = Future {
    db.withConnection { implicit connection =>
      SQL"""DELETE
            FROM users
            WHERE id_user = $id""".executeUpdate()
    }
  }(ec)

  override def update(id: Long, user: User): Future[Int] = Future {
    db.withConnection { implicit connection =>
      SQL"""UPDATE users
            SET username = ${user.username},
                email = ${user.email}
            WHERE id_user = $id""".executeUpdate()
    }
  }(ec)

  override def authenticate(username: String, password: String): Future[Option[User]] = Future {
    db.withConnection { implicit connection =>
      SQL"""SELECT id_user, username, email, password, id_role
            FROM users
            WHERE username = $username""".as(userMapper.singleOpt)
        .filter(user => BCrypt.checkpw(password, user.password.get))
    }
  }(ec)

}
