package models

import play.api.libs.json._

object Role extends Enumeration {

  type Role = Value

  val ADMIN: Value = Value(1)
  val USER: Value = Value(2)

  implicit object RoleFormat extends Format[Role] {
    override def reads(json: JsValue): JsResult[Role] =
      JsPath.read[String].map(Role.withName).reads(json)

    override def writes(role: Role): JsValue = JsString(role.toString)
  }

}
