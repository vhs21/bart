package models

case class User(
                 id: Option[Long],
                 username: String,
                 email: String,
                 password: String)
