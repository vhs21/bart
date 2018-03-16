package models

import play.api.libs.json._

object Role extends Enumeration {

  type Role = Value

  val ADMIN: Value = Value(1)
  val USER: Value = Value(2)
  val MANAGER: Value = Value(3)

  implicit object RoleFormat extends Format[Role] {
    override def reads(json: JsValue): JsResult[Role] =
      (JsPath \ "id").read[Int].map(Role.apply).reads(json)

    override def writes(role: Role): JsValue = Json.obj(
      "id" -> role.id,
      "name" -> role.toString
    )
  }

}
