package utils

import java.time.LocalDateTime

import anorm.NamedParameter
import models.ItemStatus.ItemStatus

final class ItemSearchCriteria(
                                override val limit: Long = 0,
                                override val offset: Long = 0,
                                val id: Option[Long] = None,
                                val description: Option[String] = None,
                                val registrationDate: Option[LocalDateTime] = None,
                                val idUser: Option[Long] = None,
                                val itemStatus: Option[ItemStatus] = None) extends SearchCriteria {

  override protected val whereParams: Seq[(String, NamedParameter)] = Seq(
    id.map(id => (
      "items.id_item = {id}",
      NamedParameter("id", id)
    )),
    description.map(description => (
      "items.name LIKE {description} OR items.description LIKE {description}",
      NamedParameter("description", s"%$description%")
    )),
    registrationDate.map(registrationDate => (
      "items.registration_date = {registration_date}",
      NamedParameter("registration_date", registrationDate)
    )),
    idUser.map(idUser => (
      "items.id_user = {id_user}",
      NamedParameter("id_user", idUser)
    ))
  ).flatten

}
