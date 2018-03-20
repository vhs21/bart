package repositories.impl

import anorm._
import com.google.inject.Inject
import models.Photo
import play.api.db.DBApi
import repositories.PhotoRepository

import scala.concurrent.{ExecutionContext, Future}

class PhotoRepositoryImpl @Inject()(dbapi: DBApi)(implicit val ec: ExecutionContext)
  extends ModelRepository[Photo](dbapi, Photo.parser) with PhotoRepository {

  override def selectAll: Future[Seq[Photo]] = Future.successful(Seq.empty)

  override def select(id: Long): Future[Option[Photo]] = Future.successful(None)

  override def insert(element: Photo): Future[Option[Long]] = insert(
    SQL"""INSERT INTO photos(id_photo, id_item) VALUES (${element.id}, ${element.idItem})""")

  override def delete(id: Long): Future[Option[Int]] = delete(
    SQL"""DELETE FROM photos
          WHERE id_photo = $id""")

  override def update(id: Long, element: Photo): Future[Option[Int]] = Future.successful(None)

  override def selectAllWhereItem(idItem: Long): Future[Seq[Photo]] = selectAll(
    SQL"""SELECT id_photo, id_item
          FROM photos
          WHERE id_item = $idItem""")

  override def selectOneWhereItem(idItem: Long): Future[Option[Photo]] = select(
    SQL"""SELECT id_photo, id_item
          FROM photos
          WHERE id_item = $idItem
          LIMIT 1""")

}
