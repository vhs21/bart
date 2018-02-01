package forms

import org.mindrot.jbcrypt.BCrypt
import play.api.data.Form
import play.api.data.Forms._

object SignUpForm {

  val form = Form(
    mapping(
      "username" -> nonEmptyText,
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(
                   username: String,
                   email: String,
                   password: String)

}
