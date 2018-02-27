package models

import models.Role.Role
import play.api.libs.json._

case class User(
                 id: Option[Long],
                 username: String,
                 email: String,
                 password: Option[String],
                 role: Option[Role] = Option(Role.USER))

object User {

  implicit object UserFormat extends Format[User] {
    override def writes(user: User): JsValue = Json.obj(
      "id" -> user.id,
      "username" -> user.username,
      "email" -> user.email,
      "role" -> user.role
    )

    override def reads(json: JsValue): JsResult[User] = JsSuccess(
      User(
        id = (json \ "id").asOpt[Long],
        username = (json \ "username").as[String],
        email = (json \ "email").as[String],
        password = (json \ "password").asOpt[String],
        role = (json \ "role").asOpt[Role])
    )
  }

}
