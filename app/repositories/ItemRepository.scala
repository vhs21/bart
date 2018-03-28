package repositories

import com.google.inject.ImplementedBy
import models.Item
import repositories.impl.ItemRepositoryImpl
import utils.ItemSearchCriteria

import scala.concurrent.Future

@ImplementedBy(classOf[ItemRepositoryImpl])
trait ItemRepository extends Repository[Item] {

  def count(itemSearchCriteria: ItemSearchCriteria): Future[Int]

  def selectAll(itemSearchCriteria: ItemSearchCriteria): Future[Seq[Item]]

  def updateStatus(id: Long, idStatus: Int): Future[Int]

}
