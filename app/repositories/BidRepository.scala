package repositories

import com.google.inject.ImplementedBy
import models.Bid
import repositories.impl.BidRepositoryImpl

@ImplementedBy(classOf[BidRepositoryImpl])
trait BidRepository extends Repository[Bid] {

}
