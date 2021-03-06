package repositories.impl

import anorm._
import com.google.inject.{Inject, Singleton}
import models.{Role, User}
import org.mindrot.jbcrypt.BCrypt
import play.api.db.DBApi
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class UserRepositoryImpl @Inject()(dbapi: DBApi)(implicit val ec: ExecutionContext)
  extends ModelRepository[User](dbapi, User.parser) with UserRepository {

  override def selectAll: Future[Seq[User]] = selectAll(
    SQL"""SELECT id_user, username, email, password, id_role
          FROM users""")

  override def select(id: Long): Future[Option[User]] = select(
    SQL"""SELECT id_user, username, email, password, id_role
          FROM users
          WHERE id_user = $id""")

  override def insert(element: User): Future[Option[Long]] = insert(
    SQL"""INSERT INTO users(username, email, password, id_role) VALUES(
          ${element.username},
          ${element.email},
          ${BCrypt.hashpw(element.password.get, BCrypt.gensalt())},
          ${element.role.getOrElse(Role.USER).id})""")

  override def delete(id: Long): Future[Option[Int]] = Future.successful(None)

  override def update(id: Long, element: User): Future[Option[Int]] = update(
    SQL"""UPDATE users
          SET username = ${element.username},
              email = ${element.email}
          WHERE id_user = $id""")

  override def authenticate(username: String, password: String): Future[Option[User]] = Future {
    db.withConnection { implicit connection =>
      SQL"""SELECT id_user, username, email, password, id_role
            FROM users
            WHERE username = $username""".as(User.parser.singleOpt)
        .filter(user => BCrypt.checkpw(password, user.password.get))
    }
  }

}
