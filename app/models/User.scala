package models

import models.Role.Role

case class User(
                 id: Option[Long],
                 username: String,
                 email: String,
                 password: String,
                 role: Role = Role.USER)

object User {
  def apply(
             id: Long,
             username: String,
             email: String,
             password: String,
             roleId: Int): User = new User(Option.apply(id), username, email, password, Role(roleId))
}
