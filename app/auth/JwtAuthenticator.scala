package auth

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import com.google.inject.Inject
import models.User
import play.api.Configuration
import play.api.libs.json.Json

class JwtAuthenticator @Inject()(private val configuration: Configuration) {

  val HEADER = "jw_token"

  private val jwtSecretKey: String = configuration.get[String]("jwt.secret.key")
  private val jwtSecretAlgorithm: String = configuration.get[String]("jwt.secret.algorithm")

  def createToken(user: User): String = {
    JsonWebToken(
      JwtHeader(jwtSecretAlgorithm),
      JwtClaimsSet(Json.toJson(user).toString()),
      jwtSecretKey)
  }

  def isValidToken(jwtToken: String): Boolean =
    JsonWebToken.validate(jwtToken, jwtSecretKey)

  def decodePayload(jwtToken: String): Option[User] =
    jwtToken match {
      case JsonWebToken(header, claimsSet, signature) => Option(Json.parse(claimsSet.asJsonString).as[User])
      case _ => None
    }

}

