package models

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import models.Role.Role
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class User(
                 id: Option[Long],
                 username: String,
                 email: String,
                 password: Option[String],
                 role: Option[Role] = Option(Role.USER))

object User {

  val parser: RowParser[User] = {
    get[Option[Long]]("users.id_user") ~
      get[String]("users.username") ~
      get[String]("users.email") ~
      get[Option[String]]("users.password") ~
      get[Int]("users.id_role") map {
      case id ~ username ~ email ~ password ~ roleId =>
        User(id, username, email, password, Option(Role(roleId)))
    }
  }

  implicit object UserFormat extends Format[User] {
    override def reads(json: JsValue): JsResult[User] =
      ((JsPath \ "id").readNullable[Long]
        and (JsPath \ "username").read[String]
        and (JsPath \ "email").read[String]
        and (JsPath \ "password").readNullable[String]
        and (JsPath \ "role").readNullable[Role]) (User.apply _)
        .reads(json)

    override def writes(user: User): JsValue = Json.obj(
      "id" -> user.id,
      "username" -> user.username,
      "email" -> user.email,
      "role" -> user.role
    )
  }

}
