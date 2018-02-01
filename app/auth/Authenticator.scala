package auth

import models.User
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object Authenticator {

  val USER = "user"

  implicit val userWrites: Writes[User] =
    (user: User) => Json.obj(
      "id" -> user.id,
      "username" -> user.username,
      "email" -> user.email,
      "password" -> user.password,
      "role" -> user.role.id
    )

  implicit val userReads: Reads[User] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "username").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "password").read[String] and
      (JsPath \ "role").read[Int]
    ) ((id, username, email, password, roleId) => User(id, username, email, password, roleId))

  def serializeUser(user: User): String = Json.toJson(user).toString()

  def deserializeUser(jsonUser: String): User = Json.parse(jsonUser).as[User]

}
