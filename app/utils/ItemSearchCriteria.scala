package utils

import java.time.LocalDateTime

import anorm.NamedParameter
import models.ItemStatus.ItemStatus

class ItemSearchCriteria(
                          limit: Long = 0,
                          offset: Long = 0,
                          id: Option[Long] = None,
                          description: Option[String] = None,
                          registrationDate: Option[LocalDateTime] = None,
                          idUser: Option[Long] = None,
                          itemStatus: Option[ItemStatus] = None) {

  lazy val whereClause: String =
    if (whereParams.nonEmpty)
      whereParams.map(_._1).mkString(" WHERE ", " AND ", "")
    else ""

  lazy val limitClause = " LIMIT {limit} OFFSET {offset}"

  def namedWhereParamsList: List[NamedParameter] = whereParams.map(_._2)

  def namedLimitParamsList = List(NamedParameter("limit", limit), NamedParameter("offset", offset))

  private lazy val whereParams = List(
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
