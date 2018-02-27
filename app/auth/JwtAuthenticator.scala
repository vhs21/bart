package auth

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import com.google.inject.Inject
import models.User
import play.api.Configuration
import play.api.libs.json.Json

class JwtAuthenticator @Inject()(configuration: Configuration) {

  val HEADER = "jw_token"

  private val jwtSecretKey: String = "secretKey"
  private val jwtSecretAlgorithm: String = "HS256"

  def createToken(user: User): String = {
    val header = JwtHeader(jwtSecretAlgorithm)
    val claimsSet = JwtClaimsSet(Json.toJson(user).toString())
    JsonWebToken(header, claimsSet, jwtSecretKey)
  }

  def isValidToken(jwtToken: String): Boolean =
    JsonWebToken.validate(jwtToken, jwtSecretKey)

  def decodePayload(jwtToken: String): Option[User] =
    jwtToken match {
      case JsonWebToken(header, claimsSet, signature) => Option(Json.parse(claimsSet.asJsonString).as[User])
      case _ => None
    }

}

