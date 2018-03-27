package repositories.impl

import anorm._
import com.google.inject.Inject
import models.Bid
import play.api.db.DBApi
import repositories.BidRepository

import scala.concurrent.{ExecutionContext, Future}

class BidRepositoryImpl @Inject()(dbapi: DBApi)(implicit val ec: ExecutionContext)
  extends ModelRepository[Bid](dbapi, Bid.parser) with BidRepository {

  override def selectAll: Future[Seq[Bid]] = Future.successful(Seq.empty)

  override def select(id: Long): Future[Option[Bid]] = select(
    SQL"""SELECT bids.id_bid, bids.id_item_goal, bids.id_item_offer, bids.is_accepted
          FROM bids
          WHERE bids.id_bid = $id""")

  override def insert(element: Bid): Future[Option[Long]] = insert(
    SQL"""INSERT INTO bids(id_item_goal, id_item_offer) VALUES (${element.idItemGoal}, ${element.idItemOffer})""")

  override def delete(id: Long): Future[Option[Int]] = Future.successful(None)

  override def update(id: Long, element: Bid): Future[Option[Int]] = Future.successful(None)

  override def selectAllWhereItem(idItem: Long): Future[Seq[Bid]] = selectAll(
    SQL"""SELECT bids.id_bid, bids.id_item_goal, bids.id_item_offer, bids.is_accepted
          FROM bids
          WHERE bids.id_item_goal = $idItem""")

}
