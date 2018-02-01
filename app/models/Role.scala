package models

object Role extends Enumeration {

  type Role = Value

  val ADMIN: models.Role.Value = Value(1)
  val USER: models.Role.Value = Value(2)

}
