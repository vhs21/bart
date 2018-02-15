package models

object Role extends Enumeration {

  type Role = Value

  val ADMIN: Value = Value(1)
  val USER: Value = Value(2)
  val MANAGER: Value = Value(3)

}
