package repositories

import com.google.inject.ImplementedBy
import models.Bid
import repositories.impl.BidRepositoryImpl

import scala.concurrent.Future

@ImplementedBy(classOf[BidRepositoryImpl])
trait BidRepository extends Repository[Bid] {

  def selectAllWhereItem(idItem: Long): Future[Seq[Bid]]

}
