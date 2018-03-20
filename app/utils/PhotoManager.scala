package utils

import java.io.IOException
import java.nio.file.{Files, Paths}
import java.util.Base64

import com.google.inject.{Inject, Singleton}
import models.Photo
import play.api.Configuration
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData

@Singleton
class PhotoManager @Inject()(private val configuration: Configuration) {

  private lazy val photoUploadDir: String = configuration.get[String]("photo.dir")

  def save(photo: MultipartFormData.FilePart[TemporaryFile], idItem: Long): Photo = {
    Photo(
      photo.ref.moveTo(
        Paths.get(
          photoUploadDir,
          idItem.toString + Paths.get(photo.filename).getFileName.toString),
        replace = true).getFileName.toString,
      idItem
    )
  }

  def load(filename: String): Option[String] = {
    try {
      Some(Base64.getEncoder
        .withoutPadding()
        .encodeToString(
          Files.readAllBytes(Paths.get(photoUploadDir, filename))))
    } catch {
      case e: IOException => None
    }
  }

}
