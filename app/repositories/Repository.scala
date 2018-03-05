package repositories

import scala.concurrent.Future

trait Repository[A] {

  def selectAll: Future[Seq[A]]

  def select(id: Long): Future[Option[A]]

  def insert(element: A): Future[Option[Long]]

  def delete(id: Long): Future[Int]

  def update(id: Long, element: A): Future[Int]

}
