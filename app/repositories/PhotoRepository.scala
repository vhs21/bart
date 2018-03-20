package repositories

import com.google.inject.ImplementedBy
import models.Photo
import repositories.impl.PhotoRepositoryImpl

import scala.concurrent.Future

@ImplementedBy(classOf[PhotoRepositoryImpl])
trait PhotoRepository extends Repository[Photo] {

  def selectAllWhereItem(idItem: Long): Future[Seq[Photo]]

  def selectOneWhereItem(idItem: Long): Future[Option[Photo]]

}
